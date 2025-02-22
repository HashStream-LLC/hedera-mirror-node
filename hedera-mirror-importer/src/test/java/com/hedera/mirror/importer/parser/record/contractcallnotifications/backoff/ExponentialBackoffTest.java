package com.hedera.mirror.importer.parser.record.contractcallnotifications.backoff;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExponentialBackoffTest {
    @Test
    void getBackoffDelayInMilliseconds_firstAttempt_isCloseToBaseDelay() {
        long result = ExponentialBackoff.getBackoffDelayInMilliseconds(1000, 1);
        assertThat(result).isCloseTo(1000, Percentage.withPercentage(10));
    }

    @Test
    void getBackoffDelayInMilliseconds_firstAttempt_isCloseToLargerBaseDelay() {
        long result = ExponentialBackoff.getBackoffDelayInMilliseconds(5000L, 1);
        assertThat(result).isCloseTo(5000, Percentage.withPercentage(10));
    }

    @Test
    void getBackoffDelayInMilliseconds_secondAttempt_addsDelayExponentially() {
        long result = ExponentialBackoff.getBackoffDelayInMilliseconds(5000, 2);
        assertThat(result).isCloseTo(10000, Percentage.withPercentage(10));
    }

    @Test
    void getBackoffDelayInMilliseconds_thirdAttempt_addsDelayExponentially() {
        long result = ExponentialBackoff.getBackoffDelayInMilliseconds(5000L, 3);
        assertThat(result).isCloseTo(20000, Percentage.withPercentage(10));
    }

    @Test
    void getBackoffDelayInMilliseconds_fourthAttempt_addsDelayExponentially() {
        long result = ExponentialBackoff.getBackoffDelayInMilliseconds(5000, 4);
        assertThat(result).isCloseTo(40000, Percentage.withPercentage(10));
    }
}
