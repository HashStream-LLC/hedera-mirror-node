package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DynamoBatchWriteConverter {
    private static BatchWriteItemRequest toBatchWriteRequest(String notificationEventsTable, Stream<NotificationEvent> notificationEvents) {
        Stream<Map<String, AttributeValue>> dynamoItems = notificationEvents.map(
                DynamoNotificationEventConverter::fromNotificationEvent
        );
        Stream<WriteRequest> writeRequests = dynamoItems.map(dynamoItem ->
                WriteRequest.builder().putRequest(
                        PutRequest.builder().item(dynamoItem).build()
                ).build()
        );
        return BatchWriteItemRequest.builder()
                .requestItems(Map.of(notificationEventsTable, writeRequests.toList()))
                .build();
    }

    public static List<BatchWriteItemRequest> toBatchWriteRequests(
        String notificationEventsTable,
        Stream<NotificationEvent> notificationEvents
    ) {
        // Dynamo only allows sending messages in batches of 25
        int dynamoMaxBatchSize = 25;
        List<List<NotificationEvent>> notificationEventBatches = Batching.batchItems(
                notificationEvents,
                dynamoMaxBatchSize
        );
        return notificationEventBatches.stream().map(notificationEventBatch ->
                toBatchWriteRequest(notificationEventsTable, notificationEventBatch.stream())
        ).toList();
    }
}
