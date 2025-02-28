/*
 * Copyright (C) 2022-2025 Hedera Hashgraph, LLC
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

package com.hedera.mirror.grpc.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hedera.mirror.common.domain.DomainBuilder;
import com.hedera.mirror.common.domain.addressbook.AddressBook;
import com.hedera.mirror.common.domain.entity.EntityId;
import com.hedera.mirror.grpc.GrpcIntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class AddressBookRepositoryTest extends GrpcIntegrationTest {

    private final AddressBookRepository addressBookRepository;
    private final DomainBuilder domainBuilder;

    @Test
    void findLatestTimestamp() {
        EntityId fileId = EntityId.of(101L);
        assertThat(addressBookRepository.findLatestTimestamp(fileId.getId())).isEmpty();

        domainBuilder.addressBook().customize(a -> a.fileId(EntityId.of(999L))).persist();
        assertThat(addressBookRepository.findLatestTimestamp(fileId.getId())).isEmpty();

        AddressBook addressBook2 =
                domainBuilder.addressBook().customize(a -> a.fileId(fileId)).persist();
        assertThat(addressBookRepository.findLatestTimestamp(fileId.getId()))
                .get()
                .isEqualTo(addressBook2.getStartConsensusTimestamp());

        AddressBook addressBook3 =
                domainBuilder.addressBook().customize(a -> a.fileId(fileId)).persist();
        assertThat(addressBookRepository.findLatestTimestamp(fileId.getId()))
                .get()
                .isEqualTo(addressBook3.getStartConsensusTimestamp());
    }
}
