/*
 * Copyright (C) 2019-2025 Hedera Hashgraph, LLC
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

package com.hedera.services.store.contracts.precompile;

import static com.hedera.services.utils.IdUtils.asAccount;
import static com.hedera.services.utils.IdUtils.asToken;
import static org.junit.jupiter.api.Assertions.*;

import com.hederahashgraph.api.proto.java.AccountID;
import com.hederahashgraph.api.proto.java.TokenID;
import org.junit.jupiter.api.Test;

class FungibleTokenTransferTest {

    static final long SECOND_AMOUNT = 200;
    static final AccountID A = asAccount("0.0.2");
    static final AccountID B = asAccount("0.0.3");
    static final TokenID FUNGIBLE = asToken("0.0.555");
    static final TokenID NON_FUNGIBLE = asToken("0.0.666");

    @Test
    void createsExpectedCryptoTransfer() {
        final var fungibleTransfer = new FungibleTokenTransfer(SECOND_AMOUNT, false, FUNGIBLE, B, A);
        assertEquals(FUNGIBLE, fungibleTransfer.getDenomination());
    }
}
