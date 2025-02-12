package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

public record NotificationRuleMetadata(String id, String name, int type, String predicateValue, String chain) { }