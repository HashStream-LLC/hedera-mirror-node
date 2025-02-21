package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DynamoStreamingRule {
  public static StreamingRule toStreamingRule(Map<String, AttributeValue> dynamoRecord) {
    return new StreamingRule(
        dynamoRecord.containsKey("organizationId") ? dynamoRecord.get("organizationId").s() : "unknown",
        dynamoRecord.get("ruleId").s(),
        dynamoRecord.get("ruleName").s(),
        Integer.parseInt(dynamoRecord.get("ruleType").n()),
        dynamoRecord.get("predicateValue").s(),
        dynamoRecord.get("actionWebhookUrl").s(),
        dynamoRecord.get("disabled").bool());
  }
}
