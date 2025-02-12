package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DynamoNotificationEventConverter {
    private static final DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_INSTANT;

    /** Convert the transaction model into JSON suitable downstream in the notification events table */
    public static String SerializeToString(WrappedTransactionModel value) {
        try {
            return NotificationSerializer.NotificationObjectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, AttributeValue> fromNotificationEvent(NotificationEvent event) {
        String serializedPayload = SerializeToString(event.payload());
        String serializedConsensusTimestamp = event.consensusTimestamp().format(timestampFormatter);
        String serializedStreamsTimestamp = event.streamsTimestamp().format(timestampFormatter);
        return Map.of(
                "ruleId", AttributeValue.builder().s(event.ruleId()).build(),
                "eventId", AttributeValue.builder().s(event.eventId()).build(),
                "status", AttributeValue.builder().n(String.valueOf(event.status())).build(),
                "destinationType", AttributeValue.builder().n(String.valueOf(event.destinationType())).build(),
                "payload", AttributeValue.builder().s(serializedPayload).build(),
                "consensusTimestamp", AttributeValue.builder().s(serializedConsensusTimestamp).build(),
                "streamsTimestamp", AttributeValue.builder().s(serializedStreamsTimestamp).build()
        );
    }
}
