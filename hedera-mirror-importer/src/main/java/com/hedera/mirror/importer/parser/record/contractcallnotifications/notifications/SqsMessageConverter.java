package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class SqsMessageConverter {
    private SendMessageBatchRequestEntry toSqsBatchEntry(NotifiableQueueItem notificationRequest)
            throws JsonProcessingException {
        SendMessageBatchRequestEntry.Builder batchEntryBuilder = SendMessageBatchRequestEntry.builder();
        batchEntryBuilder.id(UUID.randomUUID().toString());
        batchEntryBuilder.messageBody(
                NotificationSerializer.NotificationObjectMapper.writeValueAsString(notificationRequest)
        );
        return batchEntryBuilder.build();
    }

    private SendMessageBatchRequest toSqsBatchRequest(String notificationQueueUrl, Stream<NotifiableQueueItem> notificationRequests) {
        Stream<SendMessageBatchRequestEntry> messageBatchEntries = notificationRequests
                .map(notificationRequest -> {
                    try {
                        return toSqsBatchEntry(notificationRequest);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        return SendMessageBatchRequest.builder()
                .queueUrl(notificationQueueUrl)
                .entries(messageBatchEntries.toArray(SendMessageBatchRequestEntry[]::new))
                .build();
    }

    public List<SendMessageBatchRequest> toSqsRequests(
            String notificationQueueUrl,
            Stream<NotificationEvent> notificationEvents
    ) {
        Stream<NotifiableQueueItem> queueItems = notificationEvents.map(
                notificationEvent -> new NotifiableQueueItem(notificationEvent.ruleId(), notificationEvent.eventId())
        );
        // SQS only allows sending messages in batches of 10
        int maxSqsBatchSize = 10;
        List<List<NotifiableQueueItem>> notificationRequestBatches = Batching.batchItems(
                queueItems,
                maxSqsBatchSize
        );
        return notificationRequestBatches.stream().map(notificationRequestsBatch ->
                toSqsBatchRequest(notificationQueueUrl, notificationRequestsBatch.stream())
        ).toList();
    }
}
