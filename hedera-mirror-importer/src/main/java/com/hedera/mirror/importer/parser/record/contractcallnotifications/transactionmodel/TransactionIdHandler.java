package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hederahashgraph.api.proto.java.TransactionID;

public class TransactionIdHandler {
    public static String Stringify(TransactionID transactionID) {
        String validStartSegment = TimestampHandler.joinSecondsAndNanosWithHyphen(
                transactionID.getTransactionValidStart()
        );
        return String.format("%s-%s", transactionID.getAccountID(), validStartSegment);
    }
}
