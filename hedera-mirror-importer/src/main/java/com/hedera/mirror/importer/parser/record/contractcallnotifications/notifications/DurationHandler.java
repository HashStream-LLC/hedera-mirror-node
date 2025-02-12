package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import java.time.Duration;

public class DurationHandler {
    public static String joinSecondsAndNanos(Duration duration) {
        long seconds = duration.getSeconds();
        int nanos = duration.getNano();
        return String.format("%d%s%d", seconds, ".", nanos);
    }
}
