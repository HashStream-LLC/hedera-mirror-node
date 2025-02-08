package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import lombok.Getter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import lombok.RequiredArgsConstructor;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class SqsClientProvider {
    private final ContractCallNotificationsProperties properties;

    private SqsClient buildSqsClient() {
        SqsClientBuilder sqsBuilder = SqsClient.builder();

        String sqsEndpoint = properties.getSqsEndpoint();
        boolean overrideSqsEndpoint = sqsEndpoint != null && !sqsEndpoint.isBlank();
        if (overrideSqsEndpoint) {
            sqsBuilder.endpointOverride(URI.create(sqsEndpoint));
        }

        return sqsBuilder.build();
    }

    @Getter(lazy=true)
    private final SqsClient sqsClient = buildSqsClient();
}
