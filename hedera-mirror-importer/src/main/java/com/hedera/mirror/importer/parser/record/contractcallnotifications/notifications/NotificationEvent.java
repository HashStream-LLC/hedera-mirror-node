package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;

import java.time.ZonedDateTime;

public record NotificationEvent(
        String ruleId,
        String eventId,
        int eventNumber,
        int status,
        int destinationType,
        WrappedTransactionModel payload,
        String payloadCompression,
        ZonedDateTime consensusDateTime,
        ZonedDateTime streamsDateTime,
        ZonedDateTime dateTime,
        long timeToLive) {}