package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hedera.mirror.common.util.DomainUtils;
import com.hedera.mirror.importer.exception.ImporterException;
import com.hedera.mirror.importer.parser.record.RecordItemListener;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications.EventId;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications.NotificationRequestConverter;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications.SqsClientProvider;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.rules.RulesFinder;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import com.hedera.mirror.importer.util.Utility;
import com.hederahashgraph.api.proto.java.TransactionBody;
import com.hederahashgraph.api.proto.java.TransactionRecord;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;

import java.util.stream.Stream;

@Log4j2
@Named
@RequiredArgsConstructor
@ConditionalOnContractCallNotificationsRecordParser
@Order(0)
public class ContractCallNotificationsListener implements RecordItemListener {

  private final ContractCallNotificationsProperties properties;
  private final SqsClientProvider sqsClientProvider;
  private final RulesFinder rulesFinder;
  private final NotificationRequestConverter notificationRequestConverter;

  private boolean isContractCallRelated(TransactionBody body) {
    return body.hasContractCall() || body.hasEthereumTransaction();
  }

  @Override
  public void onItem(RecordItem recordItem) throws ImporterException {
    TransactionBody body = recordItem.getTransactionBody();
    TransactionRecord txRecord = recordItem.getTransactionRecord();
    log.trace("Storing transaction body: {}", () -> Utility.printProtoMessage(body));
    long consensusTimestamp = DomainUtils.timestampInNanosMax(txRecord.getConsensusTimestamp());

    if(!isContractCallRelated(body)) {
      log.debug("Ignoring non contract call transaction. consensusTimestamp={}", consensusTimestamp);
      return;
    }

    log.debug("Processing transaction {}", consensusTimestamp);
    String[] contractIds = ContractIdExtractor.extractContractIds(recordItem);
    if (contractIds.length == 0) {
      log.debug("No contract ids in contract call. consensusTimestamp={}", consensusTimestamp);
      return;
    }

    String[] ruleIds = rulesFinder.getMatchedRuleIds(contractIds);

    if (ruleIds.length == 0) {
      log.debug("No matched rules. consensusTimestamp={}", consensusTimestamp);
      return;
    }

    log.debug("Found {} matched rules", ruleIds.length);
    WrappedTransactionModel transactionModel = WrappedTransactionModel.fromRecordItem(recordItem);
    String eventId = EventId.toEventId(transactionModel.metadata());

    SendMessageBatchRequest sqsRequest = notificationRequestConverter.toContractCallSqsNotificationRequests(
            properties.getNotificationsQueueUrl(),
            eventId,
            Stream.of(contractIds)
    );
    sqsClientProvider.getSqsClient().sendMessageBatch(sqsRequest);
    log.debug("Processed transaction {}", consensusTimestamp);
  }
}
