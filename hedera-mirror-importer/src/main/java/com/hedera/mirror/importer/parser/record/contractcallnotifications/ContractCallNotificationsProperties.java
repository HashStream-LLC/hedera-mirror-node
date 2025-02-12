package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties("hedera.mirror.importer.parser.record.contractcallnotifications")
public class ContractCallNotificationsProperties {
    private final boolean enabled;
    private final String sqsEndpoint;
    private final String dynamoEndpoint;
    private final String notificationsQueueUrl;
    private final String notificationsEventsTable;
    private final String streamRulesTable;
    private final String unprocessedRulesGsi;

    public ContractCallNotificationsProperties(boolean enabled,
                                               @DefaultValue("") String sqsEndpoint,
                                               @DefaultValue("") String dynamoEndpoint,
                                               String notificationsQueueUrl,
                                               String notificationEventsTable,
                                               String streamRulesTable,
                                               String unprocessedRulesGsi) {
        this.enabled = enabled;
        this.sqsEndpoint = sqsEndpoint;
        this.dynamoEndpoint = dynamoEndpoint;
        this.notificationsQueueUrl = notificationsQueueUrl;
        this.notificationsEventsTable = notificationEventsTable;
        this.streamRulesTable = streamRulesTable;
        this.unprocessedRulesGsi = unprocessedRulesGsi;
    }
}
