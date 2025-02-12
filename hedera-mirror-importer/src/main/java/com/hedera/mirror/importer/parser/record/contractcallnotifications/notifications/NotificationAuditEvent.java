package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import java.time.ZonedDateTime;

public record NotificationAuditEvent(
        String ruleId,
        String eventId,
        int eventNumber,
        int status,
        int destinationType,
        String parentEventId,
        ZonedDateTime auditDateTime,
        long timeToLive) {}