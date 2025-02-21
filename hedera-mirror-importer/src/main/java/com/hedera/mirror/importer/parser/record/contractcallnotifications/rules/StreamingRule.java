package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

public record StreamingRule(
    String organizationId,
    String ruleId,
    String ruleName,
    int ruleType,
    String predicateValue,
    String actionWebhookUrl,
    boolean disabled) {
}
