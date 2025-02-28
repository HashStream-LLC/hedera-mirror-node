/*
 * Copyright (C) 2019-2025 Hedera Hashgraph, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hedera.mirror.importer.parser;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public abstract class AbstractParserProperties implements ParserProperties {

    @NotNull
    protected BatchProperties batch = new BatchProperties();

    protected boolean enabled = true;

    @DurationMin(millis = 10L)
    @NotNull
    protected Duration frequency = Duration.ofMillis(20L);

    @DurationMin(seconds = 5)
    @NotNull
    protected Duration processingTimeout = Duration.ofSeconds(10L);

    @NotNull
    protected RetryProperties retry = new RetryProperties();

    @DurationMin(seconds = 1)
    @DurationUnit(ChronoUnit.SECONDS)
    @NotNull
    protected Duration transactionTimeout = Duration.ofSeconds(120);

    @Data
    @Validated
    public static class BatchProperties {

        @NotNull
        @DurationMin(millis = 100L)
        private Duration flushInterval = Duration.ofSeconds(2L);

        @Min(1)
        private int maxFiles = 1;

        @Min(1)
        private long maxItems = 60_000L;

        @Min(1)
        private int queueCapacity = 10;

        @NotNull
        @DurationMin(millis = 100L)
        private Duration window = Duration.ofMinutes(5L);
    }

    @Data
    @Validated
    public static class RetryProperties {

        @Min(0)
        private int maxAttempts = Integer.MAX_VALUE;

        @NotNull
        @DurationMin(millis = 500L)
        private Duration maxBackoff = Duration.ofSeconds(30L);

        @NotNull
        @DurationMin(millis = 100L)
        private Duration minBackoff = Duration.ofMillis(500L);

        @Min(1)
        private int multiplier = 2;
    }
}
