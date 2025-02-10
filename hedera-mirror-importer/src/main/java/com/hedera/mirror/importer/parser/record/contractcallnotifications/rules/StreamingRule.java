package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

public record StreamingRule(String ruleId, String predicateValue, boolean disabled) {}