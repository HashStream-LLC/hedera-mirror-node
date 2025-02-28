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

import EntityId from '../entityId';
import {nsToSecNs} from '../utils';

/**
 * BaseAllowance view model, captures the common fields of the allowance view model classes
 */
class BaseAllowanceViewModel {
  /**
   * Constructs base allowance view model
   *
   * @param {{owner: string, spender: string, amount: long, amountGranted: long, timestampRange: {begin: string, end: string}}} baseAllowance
   */
  constructor(baseAllowance) {
    this.amount = baseAllowance.amount;
    this.amount_granted = baseAllowance.amountGranted;
    this.owner = EntityId.parse(baseAllowance.owner).toString();
    this.spender = EntityId.parse(baseAllowance.spender).toString();
    this.timestamp = {
      from: nsToSecNs(baseAllowance.timestampRange.begin),
      to: nsToSecNs(baseAllowance.timestampRange.end),
    };
  }
}

export default BaseAllowanceViewModel;
