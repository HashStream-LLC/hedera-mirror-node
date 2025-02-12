package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hederahashgraph.api.proto.java.TransactionReceipt;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedExchangeRateSet(
        Optional<WrappedExchangeRate> nextRate,
        Optional<WrappedExchangeRate> currentRate
) {
    public static Optional<WrappedExchangeRateSet> fromReceipt(
            TransactionReceipt receipt
    ) {
        Optional<WrappedExchangeRate> nextRate = WrappedExchangeRate.fromReceipt(
                receipt,
                r -> r.getExchangeRate().hasNextRate(),
                r -> r.getExchangeRate().getNextRate()
        );
        Optional<WrappedExchangeRate> currentRate = WrappedExchangeRate.fromReceipt(
                receipt,
                r -> r.getExchangeRate().hasCurrentRate(),
                r -> r.getExchangeRate().getCurrentRate()
        );

        return currentRate.isEmpty() && nextRate.isEmpty() ?
                Optional.empty() :
                Optional.of(new WrappedExchangeRateSet(nextRate, currentRate));
    }
}
