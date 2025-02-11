package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DynamoStreamingRule {
    public static StreamingRule toStreamingRule(Map<String, AttributeValue> dynamoRecord) {
        return new StreamingRule(
                dynamoRecord.get("ruleId").s(),
                dynamoRecord.get("predicateValue").s(),
                dynamoRecord.get("disabled").bool()
        );
    }
}
