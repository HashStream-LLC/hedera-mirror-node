package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import com.hedera.mirror.common.domain.entity.EntityId;
import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.ContractFunctionResult;
import com.hederahashgraph.api.proto.java.ContractID;
import com.hederahashgraph.api.proto.java.ContractLoginfo;
import com.hederahashgraph.api.proto.java.TransactionBody;

import java.util.Objects;
import java.util.stream.Stream;

public class ContractIdExtractor {
    public static String[] extractContractIds(RecordItem recordItem) {
        TransactionBody transactionBody = recordItem.getTransactionBody();
        ContractFunctionResult contractCallResult = recordItem.getTransactionRecord().getContractCallResult();
        ContractID entrypointContract = contractCallResult.getContractID();
        Stream<ContractID> contractLogIds = contractCallResult.getLogInfoList().stream()
                .map(ContractLoginfo::getContractID);
        return Stream.concat(contractLogIds, Stream.of(entrypointContract))
                .filter(Objects::nonNull)
                .distinct()
                .map(contractId -> EntityId.of(contractId).toString())
                .toArray(String[]::new);
    }
}
