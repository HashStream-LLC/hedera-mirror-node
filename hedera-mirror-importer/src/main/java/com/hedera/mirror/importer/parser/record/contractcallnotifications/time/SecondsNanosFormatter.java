package com.hedera.mirror.importer.parser.record.contractcallnotifications.time;

public class SecondsNanosFormatter {
    private static String joinSecondsAndNanos(long seconds, int nanos, String delimiter) {
        return String.format("%01d%s%09d", seconds, delimiter, nanos);
    }

    public static String joinSecondsAndNanosWithPeriod(long seconds, int nanos) {
        return joinSecondsAndNanos(seconds, nanos, ".");
    }

    public static String joinSecondsAndNanosWithHyphen(long seconds, int nanos) {
        return joinSecondsAndNanos(seconds, nanos, "-");
    }
}
