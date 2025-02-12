package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.CryptoApproveAllowanceTransactionBody;

import java.util.List;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedTokenAllowance(
        String owner,
        String spender,
        long amount,
        String tokenId
) {
    public static List<WrappedTokenAllowance> fromApproveAllowances(CryptoApproveAllowanceTransactionBody approveAllowances) {
        Stream<WrappedTokenAllowance> cryptoAllowances = approveAllowances.getTokenAllowancesList().stream()
                .map(allowance -> new WrappedTokenAllowance(
                        EntityId.of(allowance.getOwner()).toString(),
                        EntityId.of(allowance.getSpender()).toString(),
                        allowance.getAmount(),
                        EntityId.of(allowance.getTokenId()).toString()
                ));
        return cryptoAllowances.toList();
    }
}