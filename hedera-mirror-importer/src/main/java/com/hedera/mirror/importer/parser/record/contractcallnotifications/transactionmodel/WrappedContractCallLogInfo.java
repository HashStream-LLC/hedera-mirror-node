package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.List;

public record WrappedContractCallLogInfo(
        String contractId,
        String bloom,
        String data,
        List<String> topic
) {}
