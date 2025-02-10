package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.mirror.common.converter.ObjectToStringSerializer;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.stream.Stream;

@Service
public class NotificationRequestConverter {
    private final ObjectMapper messageBodySerializer = ObjectToStringSerializer.OBJECT_MAPPER;

    private NotificationRequest toNotificationRequest(String ruleId) {
        // TODO - the actual logic
        return new NotificationRequest(ruleId, "TODO - get event id");
    }

    private SendMessageRequest toSqsMessage(String notificationQueueUrl, NotificationRequest notificationRequest)
            throws JsonProcessingException {
        SendMessageRequest.Builder sqsMessageBuilder = SendMessageRequest.builder();
        sqsMessageBuilder.queueUrl(notificationQueueUrl);
        sqsMessageBuilder.messageBody(messageBodySerializer.writeValueAsString(notificationRequest));
        return sqsMessageBuilder.build();
    }

    private SendMessageBatchRequestEntry toSqsBatchEntry(NotificationRequest notificationRequest)
            throws JsonProcessingException {
        SendMessageBatchRequestEntry.Builder batchEntryBuilder = SendMessageBatchRequestEntry.builder();
        batchEntryBuilder.messageBody(messageBodySerializer.writeValueAsString(notificationRequest));
        return batchEntryBuilder.build();
    }

    public SendMessageBatchRequest toContractCallSqsNotificationRequests(
            String notificationQueueUrl,
            String eventId,
            Stream<String> ruleIds
    ) {
        Stream<NotificationRequest> notificationRequests = ruleIds.map(this::toNotificationRequest);
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
}
