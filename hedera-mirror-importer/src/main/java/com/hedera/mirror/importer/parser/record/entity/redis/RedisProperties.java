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

package com.hedera.mirror.importer.parser.record.entity.redis;

import com.hedera.mirror.importer.parser.record.entity.BatchPublisherProperties;
import com.hedera.mirror.importer.parser.record.entity.ConditionOnEntityRecordParser;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConditionOnEntityRecordParser
@ConfigurationProperties("hedera.mirror.importer.parser.record.entity.redis")
public class RedisProperties implements BatchPublisherProperties {

    private boolean enabled = true;

    @Min(1)
    private int queueCapacity = 8;
}
