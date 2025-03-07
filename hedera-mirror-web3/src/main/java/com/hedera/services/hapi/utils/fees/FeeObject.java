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

package com.hedera.services.hapi.utils.fees;

public class FeeObject {

    private long nodeFee;
    private long networkFee;
    private long serviceFee;

    public FeeObject(long nodeFee, long networkFee, long serviceFee) {
        this.nodeFee = nodeFee;
        this.networkFee = networkFee;
        this.serviceFee = serviceFee;
    }

    public long getNodeFee() {
        return nodeFee;
    }

    public long getNetworkFee() {
        return networkFee;
    }

    public long getServiceFee() {
        return serviceFee;
    }

    @Override
    public String toString() {
        return "FeeObject{" + "nodeFee=" + nodeFee + ", networkFee=" + networkFee + ", serviceFee=" + serviceFee + '}';
    }
}
