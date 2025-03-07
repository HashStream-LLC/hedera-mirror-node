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

import {ContractCallTestScenarioBuilder} from './common.js';

const contract = __ENV.ERC_CONTRACT_ADDRESS;
const selector = '0xe9dc6375';
const token = __ENV.NON_FUNGIBLE_TOKEN_ADDRESS;
const serialNumber = __ENV.SERIAL_NUMBER;

const {options, run} = new ContractCallTestScenarioBuilder()
  .name('contractCallTokenURI') // use unique scenario name among all tests
  .selector(selector)
  .args([token, serialNumber])
  .to(contract)
  .build();

export {options, run};
