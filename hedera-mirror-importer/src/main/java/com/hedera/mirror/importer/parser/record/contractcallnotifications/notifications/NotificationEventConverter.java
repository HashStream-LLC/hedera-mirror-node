package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import com.hederahashgraph.api.proto.java.Timestamp;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class NotificationEventConverter {
    // 90 days represented in seconds
    private static final int timeToLiveSeconds = 90 * 24 * 60;

    public List<NotificationEvent> toNotificationEvents(
            String eventId,
            WrappedTransactionModel transactionModel,
            Stream<String> ruleIds,
            Timestamp consensusTimestamp
    ) {
        long timeToLive = ZonedDateTime.now().plusSeconds(timeToLiveSeconds).toEpochSecond();
        ZonedDateTime now = ZonedDateTime.now();
        return ruleIds.map(ruleId -> new NotificationEvent(
            ruleId,
                eventId,
                0,
                NotificationEventStatus.Success,
                DestinationType.Webhook,
                transactionModel,
                PayloadCompression.None,
                TimestampConverters.toZonedDatetime(consensusTimestamp),
                now,
                now,
                timeToLive
        )).toList();
    }

    private NotificationAuditEvent toPendingNotificationAuditEvent(NotificationEvent event) {
        return new NotificationAuditEvent(
                event.ruleId(),
                String.format("%s:0", event.eventId()),
                0,
                NotificationEventStatus.Pending,
                event.destinationType(),
                event.eventId(),
                event.dateTime(),
                event.timeToLive()
        );
    }

    /**
     * Get pairs of notification events and the corresponding "pending notification"
     * audit record
     * @param notificationEvents The notification events to combine with their audit entry
     * @return A list of pairs of events and their "pending notification" audit record
     */
    public List<NotificationEventAuditPair> toPendingNotificationPairs(
            List<NotificationEvent> notificationEvents
    ) {
        return notificationEvents.stream().map(event -> {
            NotificationAuditEvent auditEvent = toPendingNotificationAuditEvent(event);
            return new NotificationEventAuditPair(event, auditEvent);
        }).toList();
    }
}
