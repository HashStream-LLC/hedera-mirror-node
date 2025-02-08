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
    private final String notificationsQueueUrl;
    private final String sqsEndpoint;
    private final String ignorePayers;

    @Getter(lazy = true)
    private final Set<String> ignorePayersSet = ignorePayers == null ?
            ImmutableSet.of() :
            ImmutableSet.copyOf(ignorePayers.split(","));

    public ContractCallNotificationsProperties(boolean enabled,
                                               String notificationsQueueUrl,
                                               @DefaultValue("") String sqsEndpoint,
                                               String ignorePayers) {
        this.enabled = enabled;
        this.notificationsQueueUrl = notificationsQueueUrl;
        this.sqsEndpoint = sqsEndpoint;
        this.ignorePayers = ignorePayers;
    }
}
