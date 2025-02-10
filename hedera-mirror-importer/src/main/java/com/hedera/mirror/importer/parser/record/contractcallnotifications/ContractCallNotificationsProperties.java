package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Set;

@Getter
@ConfigurationProperties("hedera.mirror.importer.parser.record.contractcallnotifications")
public class ContractCallNotificationsProperties {
    private final boolean enabled;
    private final String awsEndpoint;
    private final String notificationsQueueUrl;
    private final String streamRulesTable;
    private final String newRulesGsi;
    private final String ignorePayers;

    @Getter(lazy = true)
    private final Set<String> ignorePayersSet = ignorePayers == null ?
            ImmutableSet.of() :
            ImmutableSet.copyOf(ignorePayers.split(","));

    public ContractCallNotificationsProperties(boolean enabled,
                                               @DefaultValue("") String awsEndpoint,
                                               String notificationsQueueUrl,
                                               String streamRulesTable,
                                               String newRulesGsi,
                                               String ignorePayers) {
        this.enabled = enabled;
        this.awsEndpoint = awsEndpoint;
        this.notificationsQueueUrl = notificationsQueueUrl;
        this.streamRulesTable = streamRulesTable;
        this.newRulesGsi = newRulesGsi;
        this.ignorePayers = ignorePayers;
    }
}
