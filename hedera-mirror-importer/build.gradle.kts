/*
 * Copyright (C) 2022-2025 Hedera Hashgraph, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

description = "Hedera Mirror Node Importer"

plugins { id("spring-conventions") }

dependencies {
    implementation(platform("com.google.cloud:spring-cloud-gcp-dependencies"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies"))
    implementation(platform("software.amazon.awssdk:bom"))
    implementation(project(":common"))
    implementation("com.esaulpaugh:headlong")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("com.google.cloud:spring-cloud-gcp-starter-pubsub")
    implementation("commons-io:commons-io")
    implementation("io.github.mweirauch:micrometer-jvm-extras")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.projectreactor:reactor-core")
    implementation("jakarta.inject:jakarta.inject-api")
    implementation("net.java.dev.jna:jna")
    implementation("org.apache.commons:commons-compress")
    implementation("org.apache.commons:commons-collections4")
    implementation("org.apache.velocity:velocity-engine-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.hyperledger.besu:secp256k1")
    implementation("org.msgpack:jackson-dataformat-msgpack")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-kubernetes-fabric8-leader")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-fabric8-config")
    implementation("software.amazon.awssdk:netty-nio-client")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:sts")
    runtimeOnly(
        group = "io.netty",
        name = "netty-resolver-dns-native-macos",
        classifier = "osx-aarch_64"
    )
    testImplementation(project(path = ":common", configuration = "testClasses"))
    testImplementation("com.github.vertical-blank:sql-formatter")
    testImplementation("com.playtika.testcontainers:embedded-google-pubsub")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter")
    testImplementation("commons-beanutils:commons-beanutils")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.apache.commons:commons-math3")
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.eclipse.jetty.toolchain:jetty-jakarta-servlet-api") // Used by s3proxy
    testImplementation("org.gaul:s3proxy")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    implementation(project(":protobuf"))
    implementation("org.apache.kafka:kafka-clients")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("software.amazon.awssdk:sqs")
}
