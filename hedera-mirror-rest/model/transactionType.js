/*-
 * ‌
 * Hedera Mirror Node
 * ​
 * Copyright (C) 2019 - 2021 Hedera Hashgraph, LLC
 * ​
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
 * ‍
 */

'use strict';

const _ = require('lodash');
const {InvalidArgumentError} = require('../errors/invalidArgumentError');

const transactionTypeProtoToName = {
  7: 'CONTRACTCALL',
  8: 'CONTRACTCREATEINSTANCE',
  9: 'CONTRACTUPDATEINSTANCE',
  10: 'CRYPTOADDLIVEHASH',
  11: 'CRYPTOCREATEACCOUNT',
  12: 'CRYPTODELETE',
  13: 'CRYPTODELETELIVEHASH',
  14: 'CRYPTOTRANSFER',
  15: 'CRYPTOUPDATEACCOUNT',
  16: 'FILEAPPEND',
  17: 'FILECREATE',
  18: 'FILEDELETE',
  19: 'FILEUPDATE',
  20: 'SYSTEMDELETE',
  21: 'SYSTEMUNDELETE',
  22: 'CONTRACTDELETEINSTANCE',
  23: 'FREEZE',
  24: 'CONSENSUSCREATETOPIC',
  25: 'CONSENSUSUPDATETOPIC',
  26: 'CONSENSUSDELETETOPIC',
  27: 'CONSENSUSSUBMITMESSAGE',
  28: 'UNCHECKEDSUBMIT',
  29: 'TOKENCREATION',
  31: 'TOKENFREEZE',
  32: 'TOKENUNFREEZE',
  33: 'TOKENGRANTKYC',
  34: 'TOKENREVOKEKYC',
  35: 'TOKENDELETION',
  36: 'TOKENUPDATE',
  37: 'TOKENMINT',
  38: 'TOKENBURN',
  39: 'TOKENWIPE',
  40: 'TOKENASSOCIATE',
  41: 'TOKENDISSOCIATE',
  42: 'SCHEDULECREATE',
  43: 'SCHEDULEDELETE',
  44: 'SCHEDULESIGN',
  45: 'TOKENFEESCHEDULEUPDATE',
  46: 'TOKENPAUSE',
  47: 'TOKENUNPAUSE',
};

const UKNOWN = 'UNKNOWN';

const transactionTypeNameToProto = _.invert(transactionTypeProtoToName);

const getName = (protoId) => {
  return transactionTypeProtoToName.hasOwnProperty(protoId) ? transactionTypeProtoToName[protoId] : UKNOWN;
};

const getProtoId = (transactionTypeName) => {
  if (!_.isString(transactionTypeName)) {
    throw new InvalidArgumentError(`Invalid argument ${transactionTypeName} is not a string`);
  }

  const type = transactionTypeNameToProto[transactionTypeName.toUpperCase()];
  if (type === undefined) {
    throw new InvalidArgumentError(`Invalid transaction type ${transactionTypeName.toUpperCase()}`);
  }
  return type;
};

const isValid = (transactionTypeName) => {
  if (!_.isString(transactionTypeName)) {
    return false;
  }
  return transactionTypeNameToProto.hasOwnProperty(transactionTypeName.toUpperCase());
};

module.exports = {
  isValid,
  getName,
  getProtoId,
};
