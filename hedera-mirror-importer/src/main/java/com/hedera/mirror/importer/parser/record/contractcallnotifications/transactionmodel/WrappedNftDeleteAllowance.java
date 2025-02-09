package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.List;

public record WrappedNftDeleteAllowance(
        String owner,
        List<Long> serialNumbers,
        String tokenId
) {}
