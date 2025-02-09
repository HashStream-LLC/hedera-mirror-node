package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

public record WrappedNftTransfer(
        String receiverAccountId,
        String senderAccountId,
        long serialNumber,
        String tokenId,
        boolean isApproval
) implements WrappedTokenTransfer {}
