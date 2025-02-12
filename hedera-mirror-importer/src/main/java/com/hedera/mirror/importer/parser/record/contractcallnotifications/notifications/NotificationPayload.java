package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;

public record NotificationPayload(NotificationMetadata metadata, WrappedTransactionModel content) {}
