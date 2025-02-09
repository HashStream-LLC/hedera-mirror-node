package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import java.util.List;
import java.util.Optional;

public record WrappedNftAllowance(
        String owner,
        String spender,
        List<Long> serialNumbers,
        String tokenId,
        Optional<Boolean> approvedForAll,
        Optional<String> delegatingSpender
) {}
