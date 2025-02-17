package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.time.SecondsNanosFormatter;

import java.time.ZonedDateTime;

public class ZonedDateTimeHandler {
    public static String joinSecondsAndNanos(ZonedDateTime timestamp) {
        long seconds = timestamp.toEpochSecond();
        int nanos = timestamp.getNano();
        return SecondsNanosFormatter.joinSecondsAndNanosWithPeriod(seconds, nanos);
    }
}
