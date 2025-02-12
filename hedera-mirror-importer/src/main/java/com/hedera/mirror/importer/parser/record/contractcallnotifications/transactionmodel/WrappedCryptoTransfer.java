package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.TransactionRecord;

import java.util.List;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedCryptoTransfer(
        String account,
        long amount,
        boolean isApproval
) {
    public static List<WrappedCryptoTransfer> fromTransactionRecord(TransactionRecord transactionRecord) {
        Stream<WrappedCryptoTransfer> wrappedTransfers = transactionRecord.getTransferList().getAccountAmountsList().stream().map(accountAmount ->
           new WrappedCryptoTransfer(
                   EntityId.of(accountAmount.getAccountID()).toString(),
                   accountAmount.getAmount(),
                   accountAmount.getIsApproval()
           )
        );
        return wrappedTransfers.toList();
    }
}
