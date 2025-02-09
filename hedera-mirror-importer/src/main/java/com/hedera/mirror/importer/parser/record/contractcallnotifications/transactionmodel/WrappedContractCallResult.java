package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;


import java.util.List;
import java.util.Optional;

public record WrappedContractCallResult(
        String contractId,
        long gas,
        long amount,
        String functionParameters,
        long gasUsed,
        String bloom,
        String contractCallResult,
        String errorMessage,
        Optional<String> evmAddress,
        Optional<String> senderId,
        List<WrappedContractCallLogInfo> logInfo
) {}
