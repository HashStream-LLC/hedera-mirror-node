package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.List;
import java.util.Optional;

public record WrappedAllowances(
        List<WrappedCryptoAllowance> crypto,
        List<WrappedTokenAllowance> tokens,
        List<WrappedNftAllowance> nfts,
        List<WrappedNftDeleteAllowance> nftDeletes
) {}

