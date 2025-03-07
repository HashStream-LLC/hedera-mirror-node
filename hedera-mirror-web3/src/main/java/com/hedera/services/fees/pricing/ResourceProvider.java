/*
 * Copyright (C) 2023-2025 Hedera Hashgraph, LLC
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

package com.hedera.services.fees.pricing;

/**
 * Represents the three resources providers a transaction may consume resources from, and hence owe
 * fees to.
 *
 * <p>The {@link ResourceProvider#relativeWeight()} method returns the size of the network, since
 * the network and service providers are essentially consuming resources from every node. (Unlike
 * the node- specific work done in answering a query.)
 */
public enum ResourceProvider {
    /** A single node in the network. */
    NODE {
        @Override
        public String jsonKey() {
            return "nodedata";
        }

        @Override
        public int relativeWeight() {
            return 1;
        }
    },
    /** The gossip and consensus provisions of the entire network. */
    NETWORK {
        @Override
        public String jsonKey() {
            return "networkdata";
        }

        @Override
        public int relativeWeight() {
            return NETWORK_SIZE;
        }
    },
    /** The provisions of the entire network for a specific service such as HTS. */
    SERVICE {
        @Override
        public String jsonKey() {
            return "servicedata";
        }

        @Override
        public int relativeWeight() {
            return NETWORK_SIZE;
        }
    };

    private static final int RELEASE_0160_NETWORK_SIZE = 20;
    private static final int NETWORK_SIZE = RELEASE_0160_NETWORK_SIZE;

    public abstract int relativeWeight();

    public abstract String jsonKey();
}
