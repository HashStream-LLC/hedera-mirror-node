package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;

import java.time.ZonedDateTime;

public record NotificationEvent(
    String organizationId,
    String ruleId,
    String eventId,
    int eventNumber,
    int status,
    String destination,
    int destinationType,
    NotificationPayload payload,
    String payloadCompression,
    ZonedDateTime consensusDateTime,
    ZonedDateTime streamsDateTime,
    ZonedDateTime dateTime,
    long timeToLive) {
}
