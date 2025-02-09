package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

// Concrete implementations of TokenTransfer
public record WrappedFungibleTransfer(
        String account,
        String tokenId,
        long amount,
        boolean isApproval
) implements WrappedTokenTransfer {}
