package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import java.time.ZonedDateTime;

public class ZonedDateTimeHandler {
    public static String joinSecondsAndNanos(ZonedDateTime timestamp) {
        long seconds = timestamp.toEpochSecond();
        int nanos = timestamp.getNano();
        return String.format("%d%s%d", seconds, ".", nanos);
    }
}
