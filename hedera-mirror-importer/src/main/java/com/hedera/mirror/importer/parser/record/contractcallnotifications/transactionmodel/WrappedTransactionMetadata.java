package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.*;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedTransactionMetadata(
        String consensusTimestamp,
        long chargedTxFee,
        long maxFee,
        String memo,
        Optional<String> node,
        int nonce,
        Optional<String> parentConsensusTimestamp,
        boolean scheduled,
        String transactionHash,
        String transactionId,
        String transactionType,
        String payerAccountId,
        Optional<Long> validDurationSeconds,
        String validStartTimestamp
) {
    public static WrappedTransactionMetadata fromRecordItem(RecordItem item) {
        TransactionBody transactionBody = item.getTransactionBody();
        TransactionRecord transactionRecord = item.getTransactionRecord();
        TransactionID transactionId = transactionRecord.getTransactionID();

        Optional<EntityId> nodeId = OptionalHandler.mapIfPopulated(
                transactionBody,
                TransactionBody::hasNodeAccountID,
                x -> EntityId.of(x.getNodeAccountID())
        );
        Optional<Timestamp> parentConsensusTimestamp = OptionalHandler.mapIfPopulated(
                transactionRecord,
                TransactionRecord::hasParentConsensusTimestamp,
                TransactionRecord::getParentConsensusTimestamp
        );

        Optional<Duration> validDuration = OptionalHandler.mapIfPopulated(
                transactionBody,
                TransactionBody::hasTransactionValidDuration,
                TransactionBody::getTransactionValidDuration
        );

        return new WrappedTransactionMetadata(
                TimestampHandler.joinSecondsAndNanosWithPeriod(transactionRecord.getConsensusTimestamp()),
                transactionRecord.getTransactionFee(),
                transactionBody.getTransactionFee(),
                transactionBody.getMemo(),
                nodeId.map(EntityId::toString),
                transactionId.getNonce(),
                parentConsensusTimestamp.map(TimestampHandler::joinSecondsAndNanosWithPeriod),
                transactionRecord.hasScheduleRef(),
                ByteEncoder.toBase64String(transactionRecord.getTransactionHash()),
                TransactionIdHandler.Stringify(transactionId),
                transactionBody.getDataCase().getClass().getSimpleName(),
                EntityId.of(transactionId.getAccountID()).toString(),
                validDuration.map(Duration::getSeconds),
                TimestampHandler.joinSecondsAndNanosWithPeriod(transactionId.getTransactionValidStart())
        );
    }
}
