package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

public record StreamingRule(
        String ruleId,
        String ruleName,
        int ruleType,
        String predicateValue,
        String actionWebhookUrl,
        boolean disabled) {}