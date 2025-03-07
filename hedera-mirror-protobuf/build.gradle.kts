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

import com.google.protobuf.gradle.id

description = "Hedera Mirror Node Protobuf"

plugins {
    id("com.google.protobuf")
    id("java-conventions")
}

dependencies {
    api("com.hedera.hashgraph:hedera-protobuf-java-api") { isTransitive = false }
    api("com.salesforce.servicelibs:reactor-grpc-stub")
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-stub")
    api("io.projectreactor:reactor-core")
}

protobuf {
    val protobufVersion: String by rootProject.extra
    val reactorGrpcVersion: String by rootProject.extra

    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java" }
        id("reactor") { artifact = "com.salesforce.servicelibs:reactor-grpc:$reactorGrpcVersion" }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("reactor")
            }
        }
    }
}
