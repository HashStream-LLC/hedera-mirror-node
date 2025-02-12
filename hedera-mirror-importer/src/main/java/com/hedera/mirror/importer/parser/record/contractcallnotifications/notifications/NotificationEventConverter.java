package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel.WrappedTransactionModel;
import com.hederahashgraph.api.proto.java.Timestamp;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class NotificationEventConverter {
    public List<NotificationEvent> toNotificationEvents(
            String eventId,
            WrappedTransactionModel transactionalModel,
            Stream<String> ruleIds,
            Timestamp consensusTimestamp
    ) {
        return ruleIds.map(ruleId -> new NotificationEvent(
            ruleId,
                eventId,
                NotificationEventStatus.Pending,
                DestinationType.Webhook,
                transactionalModel,
                TimestampConverters.toZonedDatetime(consensusTimestamp),
                ZonedDateTime.now()
        )).toList();
    }
}
