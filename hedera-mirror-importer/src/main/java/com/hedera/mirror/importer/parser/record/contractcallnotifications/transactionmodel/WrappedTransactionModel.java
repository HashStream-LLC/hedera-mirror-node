package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.Optional;

public record WrappedTransactionModel(
        WrappedTransactionMetadata metadata,
        WrappedReceipt receipt,
        WrappedTransfers transfers,
        Optional<WrappedAllowances> allowances,
        Optional<WrappedContractCallResult> contractCall,
        Optional<WrappedEthereumTransaction> ethereumTransaction
) {
}
