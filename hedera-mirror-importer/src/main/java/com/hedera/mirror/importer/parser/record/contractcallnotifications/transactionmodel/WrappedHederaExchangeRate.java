package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.Optional;

public record WrappedHederaExchangeRate(
        int centEquiv,
        int hbarEquiv,
        Optional<Long> expirationTime
) {}
