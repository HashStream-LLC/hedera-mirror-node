package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.CryptoApproveAllowanceTransactionBody;
import com.hederahashgraph.api.proto.java.NftAllowance;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedNftAllowance(
        String owner,
        String spender,
        List<Long> serialNumbers,
        String tokenId,
        Optional<Boolean> approvedForAll,
        Optional<String> delegatingSpender
) {
    private static Optional<Boolean> getApprovedForAll(NftAllowance allowance) {
        return OptionalHandler.mapIfPopulated(
                allowance,
                NftAllowance::hasApprovedForAll,
                a -> a.getApprovedForAll().getValue()
        );
    }

    private static Optional<String> getDelegatingSpender(NftAllowance allowance) {
        return OptionalHandler.mapEntityIdStringIfPopulated(
                allowance,
                NftAllowance::hasDelegatingSpender,
                a -> EntityId.of(a.getDelegatingSpender())
        );
    }

    public static List<WrappedNftAllowance> fromApproveAllowances(CryptoApproveAllowanceTransactionBody approveAllowances) {
        Stream<WrappedNftAllowance> cryptoAllowances = approveAllowances.getNftAllowancesList().stream()
                .map(allowance -> new WrappedNftAllowance(
                        EntityId.of(allowance.getOwner()).toString(),
                        EntityId.of(allowance.getSpender()).toString(),
                        allowance.getSerialNumbersList(),
                        EntityId.of(allowance.getTokenId()).toString(),
                        getApprovedForAll(allowance),
                        getDelegatingSpender(allowance)
                ));
        return cryptoAllowances.toList();
    }
}
