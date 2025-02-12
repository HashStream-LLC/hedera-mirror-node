package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hederahashgraph.api.proto.java.ExchangeRate;
import com.hederahashgraph.api.proto.java.TransactionReceipt;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Function;

public record WrappedExchangeRate(
        int centEquiv,
        int hbarEquiv,
        Optional<Long> expirationTime
) {
    public static Optional<WrappedExchangeRate> fromReceipt(
            TransactionReceipt receipt,
            Predicate<TransactionReceipt> hasExchangeRate,
            Function<TransactionReceipt, ExchangeRate> getExchangeRate
    ) {
        if (!hasExchangeRate.test(receipt)) {
            return Optional.empty();
        }
        ExchangeRate exchangeRate = getExchangeRate.apply(receipt);
        Optional<Long> expirationTime = OptionalHandler.mapIfPopulated(
                exchangeRate,
                ExchangeRate::hasExpirationTime,
                er -> er.getExpirationTime().getSeconds()
        );
        WrappedExchangeRate wrappedExchangeRate = new WrappedExchangeRate(
                exchangeRate.getCentEquiv(), exchangeRate.getHbarEquiv(), expirationTime
        );
        return Optional.of(wrappedExchangeRate);
    }
}
