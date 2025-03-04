# Running the Importer against a local HashStream stack
The HashStream fork of the importer supports running against a local instance of Postgres and AWS (Localstack). To
test in a local environment, take the following steps:

1. Go to the root directory of this repository in a terminal session
2. If desired, ensure the local data from previous runs is cleaned up. Skip this if you
want to resume where you left off.
3. Start up the local surrounding environment: `docker compose -f hedera-mirror-importer/hashstream.local.docker-compose.yml up`
4. In another terminal session also at the root of the repository run this command to launch the importer
using the `hashstream-local` launch profile. This command also injects the localstack AWS environment:
variables:
```shell
env $(cat ./hedera-mirror-importer/local-testing/localstack.env | xargs) \
  ./gradlew :importer:bootRun --args='--spring.profiles.active=hashstream-local' 
```
You can alternatively run a similar command and use the "Attach to remote JVM" IntelliJ debug configuration to debug:
```shell
env $(cat ./hedera-mirror-importer/local-testing/localstack.env | xargs) \
  ./gradlew :importer:bootRun --args='--spring.profiles.active=hashstream-local' --debug-jvm
```
5. To read messages off of the localstack SQS queue for notifications that would've been sent, you can use:
```shell
env $(cat ./hedera-mirror-importer/local-testing/localstack.env 
| xargs) \                                               
  aws sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/notification-queue --endpoint-url http://localhost:4566
```

## Running the HashStream unit tests
The unit tests for HashStream are isolated to a specific Java package. Run this command to run only
those tests and skip 'scanning'...whatever that does in normal Hedera testing:
`./gradlew :importer:test --tests 'com.hedera.mirror.importer.parser.record.contractcallnotifications.*' --no-scan`