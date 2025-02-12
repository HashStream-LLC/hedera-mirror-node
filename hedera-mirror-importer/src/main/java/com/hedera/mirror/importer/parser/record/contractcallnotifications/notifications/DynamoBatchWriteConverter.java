package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DynamoBatchWriteConverter {
    private static BatchWriteItemRequest toBatchWriteRequest(
            String notificationEventsTable,
            Stream<Map<String, AttributeValue>> dynamoDocuments) {
        Stream<WriteRequest> writeRequests = dynamoDocuments.map(dynamoDocument ->
                WriteRequest.builder().putRequest(
                        PutRequest.builder().item(dynamoDocument).build()
                ).build()
        );
        return BatchWriteItemRequest.builder()
                .requestItems(Map.of(notificationEventsTable, writeRequests.toList()))
                .build();
    }

    public static List<BatchWriteItemRequest> toBatchWriteRequests(
        String notificationEventsTable,
        Stream<Map<String, AttributeValue>> dynamoDocuments
    ) {
        // Dynamo only allows sending messages in batches of 25
        int dynamoMaxBatchSize = 25;
        List<List<Map<String, AttributeValue>>> documentBatches = Batching.batchItems(
                dynamoDocuments,
                dynamoMaxBatchSize
        );
        return documentBatches.stream().map(documentBatch ->
                toBatchWriteRequest(notificationEventsTable, documentBatch.stream())
        ).toList();
    }
}
