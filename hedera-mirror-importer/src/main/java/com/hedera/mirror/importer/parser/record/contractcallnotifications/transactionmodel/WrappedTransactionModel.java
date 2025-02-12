package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.transaction.RecordItem;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedTransactionModel(
        WrappedTransactionMetadata metadata,
        WrappedReceipt receipt,
        WrappedTransfers transfers,
        Optional<WrappedAllowances> allowances,
        Optional<WrappedContractCallResult> contractCall
) {
    public static WrappedTransactionModel fromRecordItem(RecordItem item) {
        return new WrappedTransactionModel(
                WrappedTransactionMetadata.fromRecordItem(item),
                WrappedReceipt.fromRecordItem(item),
                WrappedTransfers.fromRecordItem(item),
                WrappedAllowances.fromRecordItem(item),
                WrappedContractCallResult.fromRecordItem(item)
        );
    }
}
