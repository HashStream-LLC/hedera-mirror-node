package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

// Sealed interface for token transfers
public sealed interface WrappedTokenTransfer permits WrappedFungibleTransfer, WrappedNftTransfer {
    String tokenId();
}
