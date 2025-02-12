package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.TransactionBody;
import com.hederahashgraph.api.proto.java.TransactionReceipt;

import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedReceipt(
        String status,
        Optional<String> accountId,
        Optional<String> fileId,
        Optional<String> contractId,
        Optional<String> scheduledTransactionId,
        Optional<String> scheduleId,
        Optional<String> tokenId,
        Optional<List<Long>> serialNumbers,
        Optional<Long> newTotalSupply,
        Optional<String> topicId,
        Optional<Long> topicSequenceNumber,
        Optional<String> topicRunningHash,
        Optional<Long> topicRunningHashVersion,
        Optional<WrappedExchangeRateSet> exchangeRate
) {
    public static WrappedReceipt fromRecordItem(RecordItem item) {
        TransactionReceipt receipt = item.getTransactionRecord().getReceipt();
        TransactionBody body = item.getTransactionBody();
        boolean hasTotalSupply = body.hasTokenBurn() ||
                body.hasTokenMint() ||
                body.hasTokenWipe() ||
                receipt.getNewTotalSupply() > 0;
        boolean isConsensusSubmitMessage = body.hasConsensusSubmitMessage();

        Optional<String> accountId = OptionalHandler.mapEntityIdStringIfPopulated(
                receipt,
                TransactionReceipt::hasAccountID,
                r -> EntityId.of(r.getAccountID())
        );
        Optional<String> fileId = OptionalHandler.mapEntityIdStringIfPopulated(
                receipt,
                TransactionReceipt::hasFileID,
                r -> EntityId.of(r.getFileID())
        );
        Optional<String> contractId = OptionalHandler.mapEntityIdStringIfPopulated(
                receipt,
                TransactionReceipt::hasFileID,
                r -> EntityId.of(r.getFileID())
        );
        Optional<String> scheduledTransactionId = OptionalHandler.mapIfPopulated(
                receipt,
                TransactionReceipt::hasScheduledTransactionID,
                r -> TransactionIdHandler.Stringify(r.getScheduledTransactionID())
        );
        Optional<String> scheduleId = OptionalHandler.mapEntityIdStringIfPopulated(
                receipt,
                TransactionReceipt::hasScheduleID,
                r -> EntityId.of(r.getScheduleID())
        );
        Optional<String> tokenId = OptionalHandler.mapEntityIdStringIfPopulated(
                receipt,
                TransactionReceipt::hasTokenID,
                r -> EntityId.of(r.getTokenID())
        );
        // TODO - this one could probably just be an empty list when
        //   no serial numbers given. Keeping it as optional for backwards compatibility
        Optional<List<Long>> serialNumbers = OptionalHandler.mapIfPopulated(
                receipt,
                r -> r.getSerialNumbersCount() > 0,
                TransactionReceipt::getSerialNumbersList
        );
        Optional<Long> totalSupply = OptionalHandler.mapIfPopulated(
                receipt,
                r -> hasTotalSupply,
                TransactionReceipt::getNewTotalSupply
        );
        Optional<String> topicId = OptionalHandler.mapEntityIdStringIfPopulated(
                receipt,
                TransactionReceipt::hasTopicID,
                r -> EntityId.of(r.getTopicID())
        );
        Optional<Long> topicSequenceNumber = OptionalHandler.mapIfPopulated(
                receipt,
                r -> isConsensusSubmitMessage,
                TransactionReceipt::getTopicSequenceNumber
        );
        Optional<String> topicRunningHash = OptionalHandler.mapIfPopulated(
                receipt,
                r -> isConsensusSubmitMessage,
                r -> ByteEncoder.toBase64String(r.getTopicRunningHash())
        );
        Optional<Long> topicRunningHashVersion = OptionalHandler.mapIfPopulated(
                receipt,
                r -> isConsensusSubmitMessage,
                TransactionReceipt::getTopicRunningHashVersion
        );

        return new WrappedReceipt(
            receipt.getStatus().name(),
            accountId,
            fileId,
            contractId,
            scheduledTransactionId,
            scheduleId,
            tokenId,
            serialNumbers,
            totalSupply,
            topicId,
            topicSequenceNumber,
            topicRunningHash,
            topicRunningHashVersion,
            WrappedExchangeRateSet.fromReceipt(receipt)
        );
    }
}