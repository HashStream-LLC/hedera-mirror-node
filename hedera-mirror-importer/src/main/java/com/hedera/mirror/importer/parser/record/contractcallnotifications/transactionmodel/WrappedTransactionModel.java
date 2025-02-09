package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.transaction.RecordItem;

import java.util.Optional;

public record WrappedTransactionModel(
        WrappedTransactionMetadata metadata,
        WrappedReceipt receipt,
        WrappedTransfers transfers,
        Optional<WrappedAllowances> allowances,
        Optional<WrappedContractCallResult> contractCall,
        Optional<WrappedEthereumTransaction> ethereumTransaction
) {
    public static WrappedTransactionModel fromRecordItem(RecordItem item) {
        // TODO - finish the implementation for allowances, contract calls, and eth transactions
        return new WrappedTransactionModel(
                WrappedTransactionMetadata.fromRecordItem(item),
                WrappedReceipt.fromRecordItem(item),
                WrappedTransfers.fromRecordItem(item),
                WrappedAllowances.fromRecordItem(item),
                Optional.empty(),
                Optional.empty()
        );
    }
}
