package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.Optional;

public record WrappedHederaExchangeRateSet(
        Optional<WrappedHederaExchangeRate> nextRate,
        Optional<WrappedHederaExchangeRate> currentRate
) {}
