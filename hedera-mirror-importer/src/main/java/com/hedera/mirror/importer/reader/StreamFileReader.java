/*
 * Copyright (C) 2020-2025 Hedera Hashgraph, LLC
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

package com.hedera.mirror.importer.reader;

import com.hedera.mirror.common.domain.StreamFile;
import com.hedera.mirror.common.domain.StreamItem;
import com.hedera.mirror.importer.domain.StreamFileData;

public interface StreamFileReader<S extends StreamFile<I>, I extends StreamItem> {

    /**
     * Reads a stream file. This method takes ownership of the {@link java.io.InputStream} provided by {@code
     * streamFileData} and will close it when it's done processing the data. Depending upon the implementation, the
     * StreamFile::getItems may return a lazily parsed list of items.
     *
     * @param streamFileData {@link StreamFileData} object for the record file.
     * @return {@link StreamFile} object
     */
    S read(StreamFileData streamFileData);
}
