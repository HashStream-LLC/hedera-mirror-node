package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.ImporterProperties;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.rules.StreamingRule;
import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import com.hederahashgraph.api.proto.java.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotificationEventConverter {
    // 90 days represented in seconds
    private static final int timeToLiveSeconds = 90 * 24 * 60;
    private final ImporterProperties _importerProperties;

    private NotificationPayload toNotificationPayload(
            ZonedDateTime currentTime,
            WrappedTransactionModel transactionModel,
            StreamingRule rule,
            Timestamp consensusTimestamp
    ) {
        ZonedDateTime consensusTimestampAsZonedDateTime = TimestampConverters.toZonedDatetime(consensusTimestamp);
        Duration timeSinceConsensus = Duration.between(consensusTimestampAsZonedDateTime, currentTime);
        NotificationMetadata metadata = new NotificationMetadata(
                new NotificationRuleMetadata(
                        rule.ruleId(),
                        rule.ruleName(),
                        rule.ruleType(),
                        rule.predicateValue(),
                        "hedera"
                ),
                _importerProperties.getNetwork().toLowerCase(),
                ZonedDateTimeHandler.joinSecondsAndNanos(currentTime),
                DurationHandler.joinSecondsAndNanos(timeSinceConsensus)
        );
        return new NotificationPayload(
                metadata,
                transactionModel
        );
    }

    public List<NotificationEvent> toNotificationEvents(
            String eventId,
            WrappedTransactionModel transactionModel,
            Stream<StreamingRule> rules,
            Timestamp consensusTimestamp
    ) {
        long timeToLive = ZonedDateTime.now().plusSeconds(timeToLiveSeconds).toEpochSecond();
        ZonedDateTime now = ZonedDateTime.now();
        return rules.map(rule -> new NotificationEvent(
                rule.ruleId(),
                eventId,
                0,
                NotificationEventStatus.Pending,
                rule.actionWebhookUrl(),
                DestinationType.Webhook,
                toNotificationPayload(
                        now,
                        transactionModel,
                        rule,
                        consensusTimestamp
                ),
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
