/*
 * Copyright (C) 2024-2025 Hedera Hashgraph, LLC
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

import _ from 'lodash';

const applyMatrix = (spec) => {
  return [false, true].map((value) => {
    const clone = _.cloneDeep(spec);
    clone.name = `${spec.name} with topicMessageLookup=${value}`;
    clone.setup.config = _.merge(clone.setup.config, {
      query: {
        topicMessageLookup: value,
      },
    });
    return clone;
  });
};

export default applyMatrix;
