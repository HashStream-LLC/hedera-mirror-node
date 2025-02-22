package com.hedera.mirror.importer.parser.record.contractcallnotifications.backoff;

import java.util.Random;

public class ExponentialBackoff {
    public static long getBackoffDelayInMilliseconds(long baseDelayInMillis, int attemptsAlreadyMade) {
        int jitter = new Random().nextInt(0, 100);
        double delayMillis = baseDelayInMillis * Math.pow(2, attemptsAlreadyMade - 1) + jitter;
        return (long) delayMillis;
    }
}
