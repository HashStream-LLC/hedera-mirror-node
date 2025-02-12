package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.ContractLoginfo;

import java.util.List;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedContractCallLogInfo(
        String contractId,
        String bloom,
        String data,
        List<String> topic
) {
    public static List<WrappedContractCallLogInfo> fromLogInfo(List<ContractLoginfo> logInfo) {
        Stream<WrappedContractCallLogInfo> wrappedLogInfo = logInfo.stream()
                .map(logInfoItem -> new WrappedContractCallLogInfo(
                        EntityId.of(logInfoItem.getContractID()).toString(),
                        ByteEncoder.toBase64String(logInfoItem.getBloom()),
                        ByteEncoder.toBase64String(logInfoItem.getData()),
                        logInfoItem.getTopicList().stream().map(ByteEncoder::toBase64String).toList()
                ));
        return wrappedLogInfo.toList();
    }
}
