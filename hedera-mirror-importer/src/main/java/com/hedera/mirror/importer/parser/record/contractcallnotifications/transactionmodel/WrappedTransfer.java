package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

public record WrappedTransfer(
        String account,
        long amount,
        boolean isApproval
) {}
