package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.ContractCallNotificationsProperties;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.dynamo.DynamoClientProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Log4j2
@Component
public class StreamingRulesLoader {
    private final DynamoDbClient _dynamoClient;
    private final StreamingRulesStore _rulesStore;
    private final ContractCallNotificationsProperties _properties;
    private boolean _initialLoadCompleted = false;
    private static final int ContractCallRuleType = 4;

    public StreamingRulesLoader(
            DynamoClientProvider dynamoClientProvider,
            StreamingRulesStore rulesStore,
            ContractCallNotificationsProperties properties
    ) {
        _dynamoClient = dynamoClientProvider.getDynamoClient();
        _rulesStore = rulesStore;
        _properties = properties;
    }

    private ScanRequest.Builder getBaseQueryBuilder(boolean getDisabledRules) {
        // TODO - eventually optimize to get at enabled, contract call rules more efficiently with indices
        //   For now, a scan with filtering is good enough
        String filterExpression = getDisabledRules ?
                "ruleType = :ruleType" :
                "(attribute_not_exists(disabled) OR disabled = :falseVal) and ruleType = :ruleType";
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":ruleType", AttributeValue.builder().n(String.valueOf(ContractCallRuleType)).build());
        if (!getDisabledRules) {
            expressionValues.put(":falseVal", AttributeValue.builder().bool(false).build());
        }
        return ScanRequest.builder()
                .tableName(_properties.getStreamRulesTable())
                .filterExpression(filterExpression)
                .expressionAttributeValues(expressionValues);
    }

    /** Load all enabled contract call rules handling Dynamo pagination */
    private List<StreamingRule> loadAllEnabledRules() {
        Map<String, AttributeValue> lastEvaluatedKey = null;
        ArrayList<StreamingRule> loadedRules = new ArrayList<>();

        do {
            ScanRequest.Builder requestBuilder = getBaseQueryBuilder(false);

            if (lastEvaluatedKey != null) {
                requestBuilder.exclusiveStartKey(lastEvaluatedKey);
            }

            ScanResponse scanResponse = _dynamoClient.scan(requestBuilder.build());

            // Process the scanned items
            scanResponse.items().forEach(item -> {
                loadedRules.add(DynamoStreamingRule.toStreamingRule(item));
            });

            // Get the last evaluated key for pagination
            lastEvaluatedKey = scanResponse.lastEvaluatedKey();
        } while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());

        return loadedRules;
    }

    private List<StreamingRule> loadRuleUpdates() {
        // TODO - handle possibility of pagination; for now, rules
        //   aren't coming in that fast
        ScanRequest request = getBaseQueryBuilder(true)
                .indexName(_properties.getUnprocessedRulesGsi())
                .build();
        ScanResponse scanResponse = _dynamoClient.scan(request);
        return scanResponse.items().stream().map(DynamoStreamingRule::toStreamingRule).toList();
    }

    private void setRulesAsProcessed(List<StreamingRule> newRules) {
        // Just doing this in a loop as:
        // 1. Bulk attribute removal wasn't working with my PartiQL statement
        // 2. I don't expect this list to get very big as processing will constantly be happening,
        // so just eat the cost of a looped single-item attribute removal
        for (StreamingRule rule : newRules) {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("ruleId", AttributeValue.builder().s(rule.ruleId()).build());

            log.info(
                    "Removing unprocessedSince from rule {}. GSI impacted: {}",
                    rule.ruleId(),
                    _properties.getUnprocessedRulesGsi()
            );
            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                    .tableName(_properties.getStreamRulesTable())
                    .key(key)
                    .updateExpression("REMOVE unprocessedSince")
                    .build();

            _dynamoClient.updateItem(updateRequest);
        }
    }

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void refreshRules() {
        if (_initialLoadCompleted) {
            List<StreamingRule> ruleUpdates = loadRuleUpdates();
            if (!ruleUpdates.isEmpty()) {
                _rulesStore.processRules(ruleUpdates);
                log.info("Rules loaded (incremental updates). {} records", ruleUpdates.size());
                setRulesAsProcessed(ruleUpdates);
            }
            else {
                log.debug("No new rules since last update");
            }
        }
        else {
            List<StreamingRule> rules = loadAllEnabledRules();
            _rulesStore.processRules(rules);
            log.info("Rules loaded (all enabled rules). {} records", rules.size());
            _initialLoadCompleted = true;
        }
    }
}
