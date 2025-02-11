package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import com.hedera.mirror.importer.parser.record.contractcallnotifications.ContractCallNotificationsProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class DynamoClientProvider {
    private final ContractCallNotificationsProperties properties;

    private DynamoDbClient buildDynamoClient() {
        DynamoDbClientBuilder dynamoClientBuilder = DynamoDbClient.builder();

        String dynamoEndpoint = properties.getDynamoEndpoint();
        boolean overrideDynamoEndpoint = dynamoEndpoint != null && !dynamoEndpoint.isBlank();
        if (overrideDynamoEndpoint) {
            dynamoClientBuilder.endpointOverride(URI.create(dynamoEndpoint));
        }

        return dynamoClientBuilder.build();
    }

    @Getter(lazy=true)
    private final DynamoDbClient dynamoClient = buildDynamoClient();
}
