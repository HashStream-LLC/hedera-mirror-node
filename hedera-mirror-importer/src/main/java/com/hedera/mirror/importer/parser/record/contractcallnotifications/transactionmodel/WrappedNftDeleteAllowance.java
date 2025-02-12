package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.CryptoDeleteAllowanceTransactionBody;

import java.util.List;
import java.util.stream.Stream;

public record WrappedNftDeleteAllowance(
        String owner,
        List<Long> serialNumbers,
        String tokenId
) {
    public static List<WrappedNftDeleteAllowance> fromDeleteAllowances(CryptoDeleteAllowanceTransactionBody deleteAllowances) {
        Stream<WrappedNftDeleteAllowance> cryptoAllowances = deleteAllowances.getNftAllowancesList().stream()
                .map(allowanceRemoval -> new WrappedNftDeleteAllowance(
                        EntityId.of(allowanceRemoval.getOwner()).toString(),
                        allowanceRemoval.getSerialNumbersList(),
                        EntityId.of(allowanceRemoval.getTokenId()).toString()
                ));
        return cryptoAllowances.toList();
    }
}
