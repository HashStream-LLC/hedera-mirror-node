package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.TransactionBody;

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
) { }