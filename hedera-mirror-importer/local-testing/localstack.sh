#!/bin/bash

set -e

endpointUrl=$DOCKER_INTERNAL_LOCALSTACK_ENDPOINT
notificationQueue=$NOTIFICATION_QUEUE_NAME
streamRulesTable=$STREAM_RULES_TABLE
ruleTypePredicateGsi=$RULE_TYPE_PREDICATE_GSI
unprocessedRulesGsi=$UNPROCESSED_RULES_GSI

echo "Bootstrapping localstack environment using endpoint: $endpointUrl"

# SQS
echo "Bootstrapping SQS"

queues=(
  "$notificationQueue"
)
for queue in ${queues[@]}; do
  aws sqs create-queue \
    --endpoint-url "$endpointUrl" \
    --queue-name "$queue"
  echo "Created queue $queue"
done

echo "Successfully bootstrapped SQS"

# Dynamo
echo "Bootstrapping DynamoDB"

aws dynamodb create-table \
  --endpoint-url "$endpointUrl" \
  --table-name "$streamRulesTable" \
  --attribute-definitions \
      AttributeName=ruleId,AttributeType=S \
      AttributeName=ruleType,AttributeType=N \
      AttributeName=predicateValue,AttributeType=S \
      AttributeName=unprocessedSince,AttributeType=S \
  --key-schema AttributeName=ruleId,KeyType=HASH \
  --provisioned-throughput 'ReadCapacityUnits=5,WriteCapacityUnits=5' \
  --global-secondary-indexes \
    "[
      {
        \"IndexName\": \"$ruleTypePredicateGsi\",
        \"KeySchema\": [
          {\"AttributeName\":\"ruleType\",\"KeyType\":\"HASH\"},
          {\"AttributeName\":\"predicateValue\",\"KeyType\":\"RANGE\"}
        ],
        \"Projection\": {
          \"ProjectionType\":\"ALL\"
        },
        \"ProvisionedThroughput\": {
          \"ReadCapacityUnits\": 5,
          \"WriteCapacityUnits\": 5
        }
      },
      {
        \"IndexName\": \"$unprocessedRulesGsi\",
        \"KeySchema\": [
          {\"AttributeName\":\"ruleId\",\"KeyType\":\"HASH\"},
          {\"AttributeName\":\"unprocessedSince\",\"KeyType\":\"RANGE\"}
        ],
        \"Projection\": {
          \"ProjectionType\":\"INCLUDE\",
          \"NonKeyAttributes\":[\"predicateValue\"]
        },
        \"ProvisionedThroughput\": {
          \"ReadCapacityUnits\": 5,
          \"WriteCapacityUnits\": 5
        }
      }
    ]"

echo "Successfully bootstrapped DynamoDB"