package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.math.BigInteger;
import java.util.Optional;

public record WrappedEthereumTransaction(
        Optional<BigInteger> gasPrice,
        Optional<BigInteger> gasLimit,
        Optional<BigInteger> maxPriorityFeePerGas,
        Optional<BigInteger> maxFeePerGas,
        BigInteger nonce,
        String to,
        BigInteger value,
        int transactionType,
        String data
) {}
