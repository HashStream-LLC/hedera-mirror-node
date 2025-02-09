package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

public record WrappedTokenAllowance(
        String owner,
        String spender,
        long amount,
        String tokenId
) {}
