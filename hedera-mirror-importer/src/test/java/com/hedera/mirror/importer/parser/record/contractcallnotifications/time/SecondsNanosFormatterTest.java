package com.hedera.mirror.importer.parser.record.contractcallnotifications.time;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SecondsNanosFormatterTest {
    @Test
    void joinSecondsAndNanosWithPeriod_withLeadingZeros() {
        String result = SecondsNanosFormatter.joinSecondsAndNanosWithPeriod(
                1739594683, 5852000
        );
        assertThat(result).isEqualTo("1739594683.005852000");
    }

    @Test
    void joinSecondsAndNanosWithPeriod_withoutLeadingZeros() {
        String result = SecondsNanosFormatter.joinSecondsAndNanosWithPeriod(
                1739594674, 618515175
        );
        assertThat(result).isEqualTo("1739594674.618515175");
    }

    @Test
    void joinSecondsAndNanosWithPeriod_secondsAtZero() {
        String result = SecondsNanosFormatter.joinSecondsAndNanosWithPeriod(
                0, 618515175
        );
        assertThat(result).isEqualTo("0.618515175");
    }
}
