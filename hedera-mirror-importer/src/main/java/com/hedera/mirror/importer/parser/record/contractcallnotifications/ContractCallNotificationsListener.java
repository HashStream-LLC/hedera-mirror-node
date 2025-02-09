package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hedera.mirror.common.util.DomainUtils;
import com.hedera.mirror.importer.exception.ImporterException;
import com.hedera.mirror.importer.parser.record.RecordItemListener;
import com.hedera.mirror.importer.util.Utility;
import com.hederahashgraph.api.proto.java.TransactionBody;
import com.hederahashgraph.api.proto.java.TransactionRecord;
import io.lworks.importer.protobuf.RecordItemOuterClass;
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
  private final NotificationRequestConverter notificationRequestConverter;

  private boolean isContractCallRelated(TransactionBody body, TransactionRecord txRecord) {
    return body.hasContractCall()
            || body.hasEthereumTransaction()
            || txRecord.hasContractCallResult()
            || txRecord.hasContractCreateResult()
            || body.hasContractCreateInstance()
            || body.hasContractDeleteInstance()
            || body.hasContractUpdateInstance();
  }

  private RecordItemOuterClass.RecordItem buildRecordItem(
          long consensusTimestamp, TransactionRecord transactionRecord, TransactionBody transactionBody) {
    return RecordItemOuterClass.RecordItem.newBuilder()
            .setConsensusTimestamp(consensusTimestamp)
            .setTransactionRecord(transactionRecord)
            .setTransactionBody(transactionBody)
            .build();
  }

  @Override
  public void onItem(RecordItem recordItem) throws ImporterException {
    TransactionBody body = recordItem.getTransactionBody();
    TransactionRecord txRecord = recordItem.getTransactionRecord();
    log.trace("Storing transaction body: {}", () -> Utility.printProtoMessage(body));
    long consensusTimestamp = DomainUtils.timeStampInNanos(txRecord.getConsensusTimestamp());

    String payerAccountId = recordItem.getPayerAccountId().toString();
    if (properties.getIgnorePayersSet().contains(payerAccountId)) {
      log.debug(
        "Ignoring transaction based on payer. consensusTimestamp={}, payerAccountId={}",
        consensusTimestamp,
        payerAccountId);
      return;
    }

    if(!isContractCallRelated(body, txRecord)) {
       log.debug(
        "Ignoring non contract call transaction. consensusTimestamp={}, payerAccountId={}",
        consensusTimestamp,
        payerAccountId);
      return;
    }

    log.debug("Processing transaction {}", consensusTimestamp);
    String[] contractIds = ContractIdExtractor.extractContractIds(recordItem);
    if (contractIds.length == 0) {
      log.debug(
              "No contract ids in contract call. Nothing to send. consensusTimestamp={}, payerAccountId={}",
              consensusTimestamp,
              payerAccountId);
      return;
    }

    SendMessageBatchRequest sqsRequest = notificationRequestConverter.toContractCallSqsNotificationRequests(
            properties.getNotificationsQueueUrl(),
            Stream.of(contractIds)
    );
    sqsClientProvider.getSqsClient().sendMessageBatch(sqsRequest);
    log.debug("Processed transaction {}", consensusTimestamp);
  }
}
