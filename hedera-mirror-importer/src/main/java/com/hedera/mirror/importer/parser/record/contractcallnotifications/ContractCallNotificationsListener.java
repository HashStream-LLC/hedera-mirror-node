package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hedera.mirror.common.util.DomainUtils;
import com.hedera.mirror.importer.exception.ImporterException;
import com.hedera.mirror.importer.parser.record.RecordItemListener;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.dynamo.DynamoClientProvider;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications.*;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.rules.RulesFinder;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import com.hedera.mirror.importer.util.Utility;
import com.hederahashgraph.api.proto.java.Timestamp;
import com.hederahashgraph.api.proto.java.TransactionBody;
import com.hederahashgraph.api.proto.java.TransactionRecord;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@Named
@RequiredArgsConstructor
@ConditionalOnContractCallNotificationsRecordParser
@Order(0)
public class ContractCallNotificationsListener implements RecordItemListener {

  private final ContractCallNotificationsProperties properties;
  private final SqsClientProvider sqsClientProvider;
  private final RulesFinder rulesFinder;
  private final NotificationEventConverter notificationEventConverter;
  private final SqsMessageConverter sqsMessageConverter;
  private final DynamoClientProvider dynamoClientProvider;

  private boolean isContractCallRelated(TransactionBody body) {
    return body.hasContractCall() || body.hasEthereumTransaction();
  }

  @Override
  public void onItem(RecordItem recordItem) throws ImporterException {
    TransactionBody body = recordItem.getTransactionBody();
    TransactionRecord txRecord = recordItem.getTransactionRecord();
    log.trace("Storing transaction body: {}", () -> Utility.printProtoMessage(body));
    Timestamp rawConsensusTimestamp = txRecord.getConsensusTimestamp();
    long consensusTimestamp = DomainUtils.timestampInNanosMax(rawConsensusTimestamp);

    log.debug("Ingesting transaction. consensusDateTime={}", consensusTimestamp);

    // Filtering to only process contract calls with associated rule(s)
    if(!isContractCallRelated(body)) {
      log.debug("Ignoring non contract call transaction. consensusDateTime={}", consensusTimestamp);
      return;
    }

    String[] contractIds = ContractIdExtractor.extractContractIds(recordItem);
    if (contractIds.length == 0) {
      log.debug("No contract ids in contract call. consensusDateTime={}", consensusTimestamp);
      return;
    }

    String[] ruleIds = rulesFinder.getMatchedRuleIds(contractIds);

    if (ruleIds.length == 0) {
      log.debug("No matched rules. consensusDateTime={}, contractIds={}", consensusTimestamp, contractIds);
      return;
    }

    log.debug(
            "Found {} matched rules. consensusDateTime={}, contractIds={}, ruleIds={}",
            ruleIds.length,
            consensusTimestamp,
            contractIds,
            ruleIds
    );

    // Confirmed this is a contract call matching rules; get transaction model
    WrappedTransactionModel transactionModel = WrappedTransactionModel.fromRecordItem(recordItem);
    String eventId = EventId.toEventId(transactionModel.metadata());

    // Get notification events
    List<NotificationEvent> notificationEvents = notificationEventConverter.toNotificationEvents(
            eventId,
            transactionModel,
            Arrays.stream(ruleIds),
            rawConsensusTimestamp
    );
    List<NotificationEventAuditPair> notificationEventPairs = notificationEventConverter.toPendingNotificationPairs(
            notificationEvents
    );
    List<Map<String, AttributeValue>> dynamoDocuments = DynamoNotificationEventConverter.fromNotificationAuditPairs(
            notificationEventPairs.stream()
    );

    // Send events to Dynamo
    List<BatchWriteItemRequest> notificationWriteRequests = DynamoBatchWriteConverter.toBatchWriteRequests(
            properties.getNotificationsEventsTable(),
            dynamoDocuments.stream()
    );
    DynamoDbClient dynamoClient = dynamoClientProvider.getDynamoClient();
    log.debug(
            "Sending notification events to Dynamo. consensusDateTime={}, dynamoTable={}",
            consensusTimestamp,
            properties.getNotificationsEventsTable()
    );
    for (BatchWriteItemRequest notificationWriteRequest : notificationWriteRequests) {
      dynamoClient.batchWriteItem(notificationWriteRequest);
    }

    // Send all notification requests to SQS to trigger processing
    List<SendMessageBatchRequest> sqsBatchRequests = sqsMessageConverter.toSqsRequests(
            properties.getNotificationsQueueUrl(),
            notificationEvents.stream()
    );
    log.debug(
            "Sending notification to SQS queue. consensusDateTime={}, queueUrl={}",
            consensusTimestamp,
            properties.getNotificationsQueueUrl()
    );
    for (SendMessageBatchRequest sqsBatchRequest : sqsBatchRequests) {
      sqsClientProvider.getSqsClient().sendMessageBatch(sqsBatchRequest);
    }

    Duration timeSinceConsensus = Duration.between(
            TimestampConverters.toInstant(rawConsensusTimestamp),
            Instant.now()
    );
    log.info(
            "Processed contract call transaction {}. Time since consensus: {} ms",
            consensusTimestamp,
            timeSinceConsensus.toMillis()
    );
  }
}
