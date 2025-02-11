package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.CryptoApproveAllowanceTransactionBody;

import java.util.List;
import java.util.stream.Stream;

public record WrappedCryptoAllowance(
        String owner,
        String spender,
        long amount
) {
    public static List<WrappedCryptoAllowance> fromApproveAllowances(CryptoApproveAllowanceTransactionBody approveAllowances) {
        Stream<WrappedCryptoAllowance> cryptoAllowances = approveAllowances.getCryptoAllowancesList().stream()
                .map(allowance -> new WrappedCryptoAllowance(
                        EntityId.of(allowance.getOwner()).toString(),
                        EntityId.of(allowance.getSpender()).toString(),
                        allowance.getAmount()
                ));
        return cryptoAllowances.toList();
    }
}
