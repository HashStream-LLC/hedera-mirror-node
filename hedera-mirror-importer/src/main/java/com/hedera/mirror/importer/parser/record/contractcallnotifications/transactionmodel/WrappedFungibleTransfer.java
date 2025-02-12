package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.TokenTransferList;
import com.hederahashgraph.api.proto.java.TransactionRecord;

import java.util.List;
import java.util.stream.Stream;

public record WrappedFungibleTransfer(
        String account,
        String tokenId,
        long amount,
        boolean isApproval
) implements WrappedTokenTransfer {
    public static Stream<WrappedFungibleTransfer> fromTokenTransferList(TokenTransferList tokenTransferList) {
        String tokenId = EntityId.of(tokenTransferList.getToken()).toString();
        return tokenTransferList.getTransfersList().stream()
                .map(accountAmount ->
                    new WrappedFungibleTransfer(
                            EntityId.of(accountAmount.getAccountID()).toString(),
                            tokenId,
                            accountAmount.getAmount(),
                            accountAmount.getIsApproval()
                    )
                );
    }

    public static List<WrappedFungibleTransfer> fromTransactionRecord(TransactionRecord transactionRecord) {
        Stream<WrappedFungibleTransfer> wrappedTransfers = transactionRecord.getTokenTransferListsList().stream()
                .flatMap(WrappedFungibleTransfer::fromTokenTransferList);
        return wrappedTransfers.toList();
    }
}
