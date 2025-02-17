package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.time.SecondsNanosFormatter;

import java.time.Duration;

public class DurationHandler {
    public static String joinSecondsAndNanos(Duration duration) {
        long seconds = duration.getSeconds();
        int nanos = duration.getNano();
        return SecondsNanosFormatter.joinSecondsAndNanosWithPeriod(seconds, nanos);
    }
}
