package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hederahashgraph.api.proto.java.CryptoApproveAllowanceTransactionBody;
import com.hederahashgraph.api.proto.java.CryptoDeleteAllowanceTransactionBody;
import com.hederahashgraph.api.proto.java.TransactionBody;

import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WrappedAllowances(
        List<WrappedCryptoAllowance> crypto,
        List<WrappedTokenAllowance> tokens,
        List<WrappedNftAllowance> nfts,
        List<WrappedNftDeleteAllowance> nftDeletes
) {
    public static Optional<WrappedAllowances> fromRecordItem(RecordItem item) {
        TransactionBody body = item.getTransactionBody();
        if (!body.hasCryptoApproveAllowance() && !body.hasCryptoDeleteAllowance()) {
            return Optional.empty();
        }

        CryptoApproveAllowanceTransactionBody approveAllowances = body.getCryptoApproveAllowance();
        CryptoDeleteAllowanceTransactionBody deleteAllowances = body.getCryptoDeleteAllowance();
        WrappedAllowances wrappedAllowances = new WrappedAllowances(
                WrappedCryptoAllowance.fromApproveAllowances(approveAllowances),
                WrappedTokenAllowance.fromApproveAllowances(approveAllowances),
                WrappedNftAllowance.fromApproveAllowances(approveAllowances),
                WrappedNftDeleteAllowance.fromDeleteAllowances(deleteAllowances)
        );
        return Optional.of(wrappedAllowances);
    }
}