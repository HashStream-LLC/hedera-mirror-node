package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.entity.EntityId;
import com.hederahashgraph.api.proto.java.TransactionID;

public class TransactionIdHandler {
    public static String Stringify(TransactionID transactionID) {
        String accountIdSegment = EntityId.of(transactionID.getAccountID()).toString();
        String validStartSegment = TimestampHandler.joinSecondsAndNanosWithHyphen(
                transactionID.getTransactionValidStart()
        );
        return String.format("%s-%s", accountIdSegment, validStartSegment);
    }
}
