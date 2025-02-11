package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;


import com.hedera.mirror.common.domain.entity.EntityId;
import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.ContractFunctionResult;
import com.hederahashgraph.api.proto.java.TransactionBody;

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
) {
    public static Optional<WrappedContractCallResult> fromRecordItem(RecordItem item) {
        TransactionBody transactionBody = item.getTransactionBody();
        boolean isRelevant = transactionBody.hasContractCall() || transactionBody.hasEthereumTransaction();
        if (!isRelevant) {
            return Optional.empty();
        }

        ContractFunctionResult contractCallResult = item.getTransactionRecord().getContractCallResult();

        Optional<String> evmAddress = OptionalHandler.mapIfPopulated(
                contractCallResult,
                ContractFunctionResult::hasEvmAddress,
                ccr -> ByteEncoder.toBase64String(ccr.getEvmAddress())
        );
        Optional<String> senderId = OptionalHandler.mapEntityIdStringIfPopulated(
                contractCallResult,
                ContractFunctionResult::hasSenderId,
                ccr -> EntityId.of(ccr.getSenderId())
        );

        WrappedContractCallResult result = new WrappedContractCallResult(
                EntityId.of(contractCallResult.getContractID()).toString(),
                contractCallResult.getGas(),
                contractCallResult.getAmount(),
                ByteEncoder.toBase64String(contractCallResult.getFunctionParameters()),
                contractCallResult.getGasUsed(),
                ByteEncoder.toBase64String(contractCallResult.getBloom()),
                ByteEncoder.toBase64String(contractCallResult.getContractCallResult()),
                contractCallResult.getErrorMessage(),
                evmAddress,
                senderId,
                WrappedContractCallLogInfo.fromLogInfo(contractCallResult.getLogInfoList())
        );
        return Optional.of(result);
    }
}