package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.Stream;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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

  public static Map<String, AttributeValue> fromNotificationEvent(NotificationEvent event) {
    String serializedPayload = SerializeToString(event.payload());
    return Map.ofEntries(
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
