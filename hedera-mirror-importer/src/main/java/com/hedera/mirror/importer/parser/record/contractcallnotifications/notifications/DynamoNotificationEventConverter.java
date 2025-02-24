package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.w3c.dom.Attr;

import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Log4j2
public class DynamoNotificationEventConverter {
  private static final DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_INSTANT;

  private static String toDateString(ZonedDateTime dateTime) {
    return dateTime.format(timestampFormatter);
  }

  /**
   * Convert the notification payload into JSON suitable downstream in the
   * notification events table
   */
  public static String SerializeToString(NotificationPayload value) {
    try {
      return NotificationSerializer.NotificationObjectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static AttributeValue toByteAttribute(byte[] value) {
    return AttributeValue.builder().b(value).build();
  }

  public static AttributeValue toStringAttribute(String value) {
    return AttributeValue.builder().s(value).build();
  }

  public static AttributeValue toStringAttribute(ZonedDateTime value) {
    return toStringAttribute(toDateString(value));
  }

  public static AttributeValue toNumberAttribute(int value) {
    return AttributeValue.builder().n(String.valueOf(value)).build();
  }

  public static AttributeValue toNumberAttribute(long value) {
    return AttributeValue.builder().n(String.valueOf(value)).build();
  }

  /**
   * Compresses a string using GZIP and returns the resulting byte array.
   */
  public static byte[] compressString(String data) throws IOException {
    if (data == null || data.isEmpty()) {
      return new byte[0];
    }
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
      gzipStream.write(data.getBytes(StandardCharsets.UTF_8));
    }
    return byteStream.toByteArray();
  }

  public static Map<String, AttributeValue> fromNotificationEvent(NotificationEvent event) {
    String serializedPayload = SerializeToString(event.payload());

    Map attributeValues = Map.ofEntries(
        Map.entry("organizationId", toStringAttribute(event.organizationId())),
        Map.entry("ruleId", toStringAttribute(event.ruleId())),
        Map.entry("eventId", toStringAttribute(event.eventId())),
        Map.entry("eventNumber", toNumberAttribute(event.eventNumber())),
        Map.entry("status", toNumberAttribute(event.status())),
        Map.entry("destinationType", toNumberAttribute(event.destinationType())),
        Map.entry("payload", toStringAttribute(serializedPayload)),
        Map.entry("consensusDateTime", toStringAttribute(event.consensusDateTime())),
        Map.entry("streamsDateTime", toStringAttribute(event.streamsDateTime())),
        Map.entry("timeToLive", toNumberAttribute(event.timeToLive())),
        Map.entry("payloadCompression", toStringAttribute(event.payloadCompression())),
        Map.entry("dateTime", toStringAttribute(ZonedDateTime.now())));

    if (serializedPayload.length() > 2000) {
      try {
        byte[] compressedPayload = compressString(serializedPayload);
        attributeValues.put("payload", toByteAttribute(compressedPayload));
        attributeValues.put("compressedPayload", toStringAttribute("gzip"));
      } catch (IOException e) {
        log.error("Failed to compress payload for event {}", event.eventId(), e);
        attributeValues.put("payload", toStringAttribute(serializedPayload));
        attributeValues.put("compressedPayload", toStringAttribute(event.payloadCompression()));
      }
    }

    return attributeValues;
  }

  public static Map<String, AttributeValue> fromNotificationAuditEvent(NotificationAuditEvent event) {
    return Map.ofEntries(
        Map.entry("ruleId", toStringAttribute(event.ruleId())),
        Map.entry("eventId", toStringAttribute(event.eventId())),
        Map.entry("parentEventId", toStringAttribute(event.parentEventId())),
        Map.entry("eventNumber", toNumberAttribute(event.eventNumber())),
        Map.entry("status", toNumberAttribute(event.status())),
        Map.entry("destinationType", toNumberAttribute(event.destinationType())),
        Map.entry("auditDateTime", toStringAttribute(event.auditDateTime())),
        Map.entry("timeToLive", toNumberAttribute(event.timeToLive())));
  }

  private static java.util.stream.Stream<Map<String, AttributeValue>> fromNotificationAuditPair(
      NotificationEventAuditPair eventAuditPair) {
    return java.util.stream.Stream.of(
        fromNotificationEvent(eventAuditPair.event()),
        fromNotificationAuditEvent(eventAuditPair.auditEvent()));
  }

  public static List<Map<String, AttributeValue>> fromNotificationAuditPairs(
      java.util.stream.Stream<NotificationEventAuditPair> eventAuditPairs) {
    return eventAuditPairs.flatMap(DynamoNotificationEventConverter::fromNotificationAuditPair).toList();
  }
}
