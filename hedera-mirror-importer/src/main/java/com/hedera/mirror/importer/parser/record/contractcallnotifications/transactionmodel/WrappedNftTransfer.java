package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.TokenTransferList;
import com.hederahashgraph.api.proto.java.TransactionRecord;

import java.util.List;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedNftTransfer(
        String receiverAccountId,
        String senderAccountId,
        long serialNumber,
        String tokenId,
        boolean isApproval
) implements WrappedTokenTransfer {
    public static Stream<WrappedNftTransfer> fromTokenTransferList(TokenTransferList tokenTransferList) {
        String tokenId = EntityId.of(tokenTransferList.getToken()).toString();
        return tokenTransferList.getNftTransfersList().stream()
                .map(nftTransfer ->
                        new WrappedNftTransfer(
                                EntityId.of(nftTransfer.getReceiverAccountID()).toString(),
                                EntityId.of(nftTransfer.getSenderAccountID()).toString(),
                                nftTransfer.getSerialNumber(),
                                tokenId,
                                nftTransfer.getIsApproval()
                        )
                );
    }

    public static List<WrappedNftTransfer> fromTransactionRecord(TransactionRecord transactionRecord) {
        Stream<WrappedNftTransfer> wrappedTransfers = transactionRecord.getTokenTransferListsList().stream()
                .flatMap(WrappedNftTransfer::fromTokenTransferList);
        return wrappedTransfers.toList();
    }
}
