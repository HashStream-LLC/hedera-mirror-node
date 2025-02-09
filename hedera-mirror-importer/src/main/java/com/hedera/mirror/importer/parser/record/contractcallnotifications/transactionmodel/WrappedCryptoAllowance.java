package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

public record WrappedCryptoAllowance(
        String owner,
        String spender,
        long amount
) {}
