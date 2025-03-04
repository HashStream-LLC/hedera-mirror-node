/*
 * Copyright (C) 2025 Hedera Hashgraph, LLC
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

import com.hedera.mirror.common.domain.entity.Node;
import com.hedera.mirror.common.domain.transaction.RecordItem;
import com.hedera.mirror.common.domain.transaction.Transaction;
import com.hedera.mirror.common.domain.transaction.TransactionType;
import com.hedera.mirror.importer.ImporterProperties;
import com.hedera.mirror.importer.config.Owner;
import com.hedera.mirror.importer.parser.record.entity.EntityProperties;
import com.hedera.mirror.importer.parser.record.transactionhandler.AbstractNodeTransactionHandler;
import com.hederahashgraph.api.proto.java.TransactionRecord;
import io.hypersistence.utils.hibernate.type.range.guava.PostgreSQLGuavaRangeType;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

@Named
public class FixNodeTransactionsMigration extends ConfigurableJavaMigration {
    // Earliest consensus timestamp to consider
    private static final long LOWER_TIMESTAMP = 1733961600000000000L;

    private static final String DROP_DATA_SQL =
            """
            truncate node;
            truncate node_history;
            """;
    private static final String NODE_TRANSACTIONS_SQL =
            """
            select consensus_timestamp, transaction_bytes, transaction_record_bytes
            from transaction
            where consensus_timestamp >= ? and type in (54, 55, 56)
            order by consensus_timestamp asc;
            """;

    private static final String INSERT_SQL =
            """
            insert into %s (node_id, created_timestamp, deleted, admin_key, timestamp_range)
            values (?, ?, ?, ?, ?::int8range);
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectProvider<AbstractNodeTransactionHandler> nodeTransactionHandlers;
    private final EntityProperties entityProperties;
    private final Map<TransactionType, AbstractNodeTransactionHandler> nodeTransactionHandlerMap =
            new EnumMap<>(TransactionType.class);
    private final boolean v2;

    @Lazy
    FixNodeTransactionsMigration(
            Environment environment,
            ObjectProvider<AbstractNodeTransactionHandler> nodeTransactionHandlers,
            ImporterProperties importerProperties,
            EntityProperties entityProperties,
            @Owner JdbcTemplate jdbcTemplate) {
        super(importerProperties.getMigration());
        this.v2 = environment.acceptsProfiles(Profiles.of("v2"));
        this.nodeTransactionHandlers = nodeTransactionHandlers;
        this.entityProperties = entityProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doMigrate() throws IOException {
        if (!entityProperties.getPersist().isNodes()) {
            log.info("Node entities are not persisted. Skipping migration.");
            return;
        }

        var nodeRecordItems = getRecordItems();
        if (nodeRecordItems.isEmpty()) {
            log.info("No node transactions to fix. Skipping migration.");

            return;
        }
        var nodeState = new HashMap<Long, Node>();
        var historicalNodes = new ArrayList<Node>();

        for (var recordItem : nodeRecordItems) {
            var nodeEntity = recordItemToNode(recordItem);

            var state = nodeState.get(nodeEntity.getNodeId());
            if (state != null) {
                historicalNodes.add(state);
            }

            nodeState.put(nodeEntity.getNodeId(), mergeNode(state, nodeEntity));
        }

        ParameterizedPreparedStatementSetter<Node> statementSetter = (ps, node) -> {
            ps.setLong(1, node.getNodeId());
            ps.setObject(2, node.getCreatedTimestamp(), java.sql.Types.BIGINT);
            ps.setBoolean(3, node.isDeleted());
            ps.setBytes(4, node.getAdminKey());
            ps.setString(5, PostgreSQLGuavaRangeType.INSTANCE.asString(node.getTimestampRange()));
        };

        jdbcTemplate.execute(DROP_DATA_SQL);
        jdbcTemplate.batchUpdate(INSERT_SQL.formatted("node"), nodeState.values(), nodeState.size(), statementSetter);
        jdbcTemplate.batchUpdate(
                INSERT_SQL.formatted("node_history"), historicalNodes, historicalNodes.size(), statementSetter);

        log.info(
                "Successfully processed {} node transactions producing {} rows and {} history rows",
                nodeRecordItems.size(),
                nodeState.size(),
                historicalNodes.size());
    }

    private Node recordItemToNode(RecordItem recordItem) {
        var type = TransactionType.of(recordItem.getTransactionType());
        var handler = nodeTransactionHandlerMap.computeIfAbsent(type, t -> nodeTransactionHandlers.stream()
                .filter(h -> h.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No handler found for transaction type: " + t)));

        return handler.parseNode(recordItem);
    }

    @SneakyThrows
    private RecordItem toRecordItem(Transaction transaction) {
        var protoTransaction =
                com.hederahashgraph.api.proto.java.Transaction.parseFrom(transaction.getTransactionBytes());
        var protoRecord = TransactionRecord.parseFrom(transaction.getTransactionRecordBytes());
        return RecordItem.builder()
                .transaction(protoTransaction)
                .transactionRecord(protoRecord)
                .build();
    }

    @Override
    public MigrationVersion getVersion() {
        return v2 ? MigrationVersion.fromVersion("2.8.1") : MigrationVersion.fromVersion("1.103.1");
    }

    @Override
    public String getDescription() {
        return "Add missing node information from node transactions";
    }

    private List<RecordItem> getRecordItems() {
        return jdbcTemplate
                .query(NODE_TRANSACTIONS_SQL, new DataClassRowMapper<>(Transaction.class), LOWER_TIMESTAMP)
                .stream()
                .map(this::toRecordItem)
                .toList();
    }

    private Node mergeNode(Node previous, Node current) {
        if (previous != null) {
            previous.setTimestampUpper(current.getTimestampLower());
            current.setCreatedTimestamp(previous.getCreatedTimestamp());

            if (current.getAdminKey() == null) {
                current.setAdminKey(previous.getAdminKey());
            }
        }

        return current;
    }
}
