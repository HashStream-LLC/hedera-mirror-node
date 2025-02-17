package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.time.SecondsNanosFormatter;
import com.hederahashgraph.api.proto.java.Timestamp;

public class TimestampHandler {
    public static String joinSecondsAndNanosWithPeriod(Timestamp consensusTimestamp) {
        return SecondsNanosFormatter.joinSecondsAndNanosWithPeriod(
                consensusTimestamp.getSeconds(),
                consensusTimestamp.getNanos()
        );
    }

    public static String joinSecondsAndNanosWithHyphen(Timestamp consensusTimestamp) {
        return SecondsNanosFormatter.joinSecondsAndNanosWithHyphen(
                consensusTimestamp.getSeconds(),
                consensusTimestamp.getNanos()
        );
    }
}
