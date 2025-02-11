package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hederahashgraph.api.proto.java.Timestamp;

public class TimestampHandler {
    private static String joinSecondsAndNanos(Timestamp consensusTimestamp, String delimiter) {
        long seconds = consensusTimestamp.getSeconds();
        int nanos = consensusTimestamp.getNanos();
        return String.format("%d%s%d", seconds, delimiter, nanos);
    }

    public static String joinSecondsAndNanosWithPeriod(Timestamp consensusTimestamp) {
        return joinSecondsAndNanos(consensusTimestamp, ".");
    }

    public static String joinSecondsAndNanosWithHyphen(Timestamp consensusTimestamp) {
        return joinSecondsAndNanos(consensusTimestamp, "-");
    }
}
