package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.ContractFunctionResult;
import com.hederahashgraph.api.proto.java.TransactionBody;

import java.util.stream.Stream;

public class ContractIdExtractor {
    public static String[] extractContractIds(RecordItem recordItem) {
        TransactionBody transactionBody = recordItem.getTransactionBody();
        if (!transactionBody.hasContractCall() && !transactionBody.hasEthereumTransaction()) {
            return new String[0];
        }
        ContractFunctionResult contractCallResult = recordItem.getTransactionRecord().getContractCallResult();
        String entrypointContract = contractCallResult.getContractID().toString();
        Stream<String> contractLogIds = contractCallResult.getLogInfoList().stream()
                .map(logInfo -> logInfo.getContractID().toString());
        return Stream.concat(contractLogIds, Stream.of(entrypointContract))
                .filter(contractId -> !contractId.isBlank())
                .distinct()
                .toArray(String[]::new);
    }
}
