/*
 * Copyright (C) 2023-2025 Hedera Hashgraph, LLC
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

package com.hedera.mirror.importer.migration;

import com.hedera.mirror.importer.db.PartitionMaintenance;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Configuration for migration tests whose target is before the migration that creates account_balance and token_balance
 * partitions. Purpose is to disable partition maintenance job by a mock bean so the test application context won't fail
 * to start.
 */
@TestConfiguration
public class DisablePartitionMaintenanceConfiguration {

    @MockBean
    private PartitionMaintenance partitionMaintenance;
}
