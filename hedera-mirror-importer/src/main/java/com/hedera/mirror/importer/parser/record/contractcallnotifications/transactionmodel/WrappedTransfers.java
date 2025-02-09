package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;


import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.TransactionRecord;

import java.util.List;

public record WrappedTransfers(
        List<WrappedCryptoTransfer> crypto,
        List<WrappedFungibleTransfer> tokens,
        List<WrappedNftTransfer> nfts
) {
    public static WrappedTransfers fromRecordItem(RecordItem item) {
        TransactionRecord transactionRecord = item.getTransactionRecord();

        return new WrappedTransfers(
                WrappedCryptoTransfer.fromTransactionRecord(transactionRecord),
                WrappedFungibleTransfer.fromTransactionRecord(transactionRecord),
                WrappedNftTransfer.fromTransactionRecord(transactionRecord)
        );
    }
}