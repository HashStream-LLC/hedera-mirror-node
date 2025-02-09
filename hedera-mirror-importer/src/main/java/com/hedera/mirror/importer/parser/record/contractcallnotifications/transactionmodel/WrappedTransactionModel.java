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
        return new WrappedTransactionModel(
                WrappedTransactionMetadata.fromRecordItem(item),
                WrappedReceipt.fromRecordItem(item),
                WrappedTransfers.fromRecordItem(item),
                WrappedAllowances.fromRecordItem(item),
                WrappedContractCallResult.fromRecordItem(item),
                // TODO - determine if we actually need EVM transaction data or if this was purely for arbitrage
                Optional.empty()
        );
    }
}
