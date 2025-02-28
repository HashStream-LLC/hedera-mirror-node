/*
 * Copyright (C) 2024-2025 Hedera Hashgraph, LLC
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

package com.hedera.mirror.restjava.mapper;

import static com.hedera.mirror.restjava.mapper.CommonMapper.QUALIFIER_TIMESTAMP;

import com.hedera.mirror.common.domain.entity.Entity;
import com.hedera.mirror.rest.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class)
public interface TopicMapper {

    @Mapping(source = "autoRenewAccountId", target = "autoRenewAccount")
    @Mapping(source = "createdTimestamp", target = "createdTimestamp", qualifiedByName = QUALIFIER_TIMESTAMP)
    @Mapping(source = "id", target = "topicId")
    @Mapping(source = "key", target = "adminKey")
    @Mapping(source = "timestampRange", target = "timestamp")
    Topic map(Entity source);
}
