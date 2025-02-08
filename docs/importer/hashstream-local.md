# Running the Importer against a local HashStream stack
The HashStream fork of the importer supports running against a local instance of Postgres and AWS (Localstack). To
test in a local environment, take the following steps:

1. Go to the root directory of this repository in a terminal session
2. Start up the local surrounding environment: `docker compose -f hedera-mirror-importer/hashstream.local.docker-compose.yml up`
3. In another terminal session also at the root of the repository run this command to launch the importer
using the `hashstream-local` launch profile. This command also injects the localstack AWS environment:
variables:
```sh
env $(cat ./hedera-mirror-importer/local-testing/localstack.env | xargs) \
  ./gradlew :importer:bootRun --args='--spring.profiles.active=hashstream-local' 
```