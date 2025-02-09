package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;


import java.util.List;

public record WrappedTransfers(
        List<WrappedTransfers> crypto,
        List<WrappedFungibleTransfer> tokens,
        List<WrappedNftTransfer> nfts
) {}