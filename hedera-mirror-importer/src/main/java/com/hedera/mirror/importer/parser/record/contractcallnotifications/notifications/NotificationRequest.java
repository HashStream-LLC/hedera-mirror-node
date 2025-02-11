package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;

public record NotificationRequest(String ruleId, String eventId, WrappedTransactionModel payload) {}