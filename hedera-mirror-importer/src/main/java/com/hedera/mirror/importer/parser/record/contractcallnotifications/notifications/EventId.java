package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionMetadata;

public class EventId {
    public static String toEventId(WrappedTransactionMetadata transactionMetadata) {
        return String.format("%s:%s", transactionMetadata.transactionId(), transactionMetadata.consensusTimestamp());
    }
}
