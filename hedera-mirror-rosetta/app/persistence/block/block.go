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

package block

import (
	"database/sql"
	"errors"
	"sync"

	rTypes "github.com/coinbase/rosetta-sdk-go/types"
	"github.com/hashgraph/hedera-mirror-node/hedera-mirror-rosetta/app/domain/types"
	hErrors "github.com/hashgraph/hedera-mirror-node/hedera-mirror-rosetta/app/errors"
	log "github.com/sirupsen/logrus"
	"gorm.io/gorm"
)

const (
	genesisConsensusStartUnset = -1

	// selectSecondLatestWithIndex - Selects the second latest record block
	selectSecondLatestWithIndex string = `select consensus_start,
                                           consensus_end,
                                           hash,
                                           index,
                                           prev_hash,
                                           (index + 1) latest_index
                                    from record_file
                                    where index < (select max(index) from record_file)
                                    order by index desc
                                    limit 1`

	// selectByHashWithIndex - Selects the row by given hash
	selectByHashWithIndex string = `SELECT consensus_start,
                                           consensus_end,
                                           hash,
                                           index,
                                           prev_hash,
                                            (select max(index) from record_file) latest_index
                                    FROM record_file
                                    WHERE hash = @hash`

	// selectGenesis - Selects the first block whose consensus_end is after the genesis account balance
	// timestamp. Return the record file with adjusted consensus start
	selectGenesis string = `select
                              hash,
                              index,
                              case
                                when genesis.min >= rf.consensus_start then genesis.min + 1
                                else rf.consensus_start
                              end consensus_start
                            from record_file rf
                            join (select min(consensus_timestamp) from account_balance_file) genesis
                              on consensus_end > genesis.min
                            order by consensus_end
                            limit 1`

	// selectRecordBlockByIndex - Selects the record block by its index
	selectRecordBlockByIndex string = `SELECT consensus_start,
                                             consensus_end,
                                             hash,
                                             index,
                                             prev_hash,
                                             (select max(index) from record_file) latest_index
                                      FROM record_file
                                      WHERE index = @index`
)

type recordBlock struct {
	ConsensusStart int64
	ConsensusEnd   int64
	Hash           string
	Index          int64
	LatestIndex    int64
	PrevHash       string
}

func (rb *recordBlock) ToBlock(genesisConsensusStart int64, genesisIndex int64) *types.Block {
	consensusStart := rb.ConsensusStart
	index := rb.Index - genesisIndex
	parentIndex := index - 1
	parentHash := rb.PrevHash

	// Handle the edge case for querying first block
	if parentIndex < 0 {
		consensusStart = genesisConsensusStart
		parentIndex = 0      // Parent index should be 0, same as current block index
		parentHash = rb.Hash // Parent hash should be same as current block hash
	}

	return &types.Block{
		Index:               index,
		Hash:                rb.Hash,
		LatestIndex:         rb.LatestIndex - genesisIndex,
		ParentIndex:         parentIndex,
		ParentHash:          parentHash,
		ConsensusStartNanos: consensusStart,
		ConsensusEndNanos:   rb.ConsensusEnd,
	}
}

// blockRepository struct that has connection to the Database
type blockRepository struct {
	once                   sync.Once
	dbClient               *gorm.DB
	genesisConsensusStart  int64
	genesisRecordFileIndex int64
}

// NewBlockRepository creates an instance of a blockRepository struct
func NewBlockRepository(dbClient *gorm.DB) *blockRepository {
	return &blockRepository{dbClient: dbClient, genesisConsensusStart: genesisConsensusStartUnset}
}

// FindByHash retrieves a block by a given Hash
func (br *blockRepository) FindByHash(hash string) (*types.Block, *rTypes.Error) {
	if hash == "" {
		return nil, hErrors.ErrInvalidArgument
	}

	if err := br.initGenesisRecordFile(); err != nil {
		return nil, err
	}

	return br.findBlockByHash(hash)
}

// FindByIdentifier retrieves a block by Index && Hash
func (br *blockRepository) FindByIdentifier(index int64, hash string) (*types.Block, *rTypes.Error) {
	if index < 0 || hash == "" {
		return nil, hErrors.ErrInvalidArgument
	}

	if err := br.initGenesisRecordFile(); err != nil {
		return nil, err
	}

	block, err := br.findBlockByHash(hash)
	if err != nil {
		return nil, err
	}

	if block.Index != index {
		return nil, hErrors.ErrBlockNotFound
	}

	return block, nil
}

// FindByIndex retrieves a block by given Index
func (br *blockRepository) FindByIndex(index int64) (*types.Block, *rTypes.Error) {
	if index < 0 {
		return nil, hErrors.ErrInvalidArgument
	}

	if err := br.initGenesisRecordFile(); err != nil {
		return nil, err
	}

	return br.findBlockByIndex(index)
}

// RetrieveGenesis retrieves the genesis block
func (br *blockRepository) RetrieveGenesis() (*types.Block, *rTypes.Error) {
	if err := br.initGenesisRecordFile(); err != nil {
		return nil, err
	}

	return br.findBlockByIndex(0)
}

// RetrieveLatest retrieves the second latest block. It's required to hide the latest block so account service can
// add 0-amount genesis token balance to a block for tokens whose first transfer to the account is in the next block
func (br *blockRepository) RetrieveLatest() (*types.Block, *rTypes.Error) {
	if err := br.initGenesisRecordFile(); err != nil {
		return nil, err
	}

	rb := &recordBlock{}
	if err := br.dbClient.Raw(selectSecondLatestWithIndex).First(rb).Error; err != nil {
		return nil, handleDatabaseError(err, hErrors.ErrBlockNotFound)
	}

	if rb.Index < br.genesisRecordFileIndex {
		return nil, hErrors.ErrBlockNotFound
	}

	return rb.ToBlock(br.genesisConsensusStart, br.genesisRecordFileIndex), nil
}

func (br *blockRepository) findBlockByIndex(index int64) (*types.Block, *rTypes.Error) {
	rb := &recordBlock{}
	index += br.genesisRecordFileIndex
	if err := br.dbClient.Raw(selectRecordBlockByIndex, sql.Named("index", index)).First(rb).Error; err != nil {
		return nil, handleDatabaseError(err, hErrors.ErrBlockNotFound)
	}

	return rb.ToBlock(br.genesisConsensusStart, br.genesisRecordFileIndex), nil
}

func (br *blockRepository) findBlockByHash(hash string) (*types.Block, *rTypes.Error) {
	rb := &recordBlock{}
	if err := br.dbClient.Raw(selectByHashWithIndex, sql.Named("hash", hash)).First(rb).Error; err != nil {
		return nil, handleDatabaseError(err, hErrors.ErrBlockNotFound)
	}

	return rb.ToBlock(br.genesisConsensusStart, br.genesisRecordFileIndex), nil
}

func (br *blockRepository) initGenesisRecordFile() *rTypes.Error {
	if br.genesisConsensusStart != genesisConsensusStartUnset {
		return nil
	}

	rb := &recordBlock{}
	if err := br.dbClient.Raw(selectGenesis).First(rb).Error; err != nil {
		return handleDatabaseError(err, hErrors.ErrNodeIsStarting)
	}

	br.once.Do(func() {
		br.genesisConsensusStart = rb.ConsensusStart
		br.genesisRecordFileIndex = rb.Index
	})

	log.Infof("Fetched genesis record file, index - %d", rb.Index)
	return nil
}

func handleDatabaseError(err error, recordNotFoundErr *rTypes.Error) *rTypes.Error {
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return recordNotFoundErr
	}

	log.Errorf("%s: %s", hErrors.ErrDatabaseError.Message, err)
	return hErrors.ErrDatabaseError
}
