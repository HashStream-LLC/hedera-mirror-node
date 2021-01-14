package com.hedera.mirror.importer.reader.signature;

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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import lombok.extern.log4j.Log4j2;

import com.hedera.mirror.importer.domain.FileStreamSignature;
import com.hedera.mirror.importer.domain.FileStreamSignature.SignatureType;
import com.hedera.mirror.importer.exception.SignatureFileParsingException;

@Log4j2
@Named
public class SignatureFileReaderV2 extends AbstractSignatureFileReader {

    protected static final byte SIGNATURE_TYPE_SIGNATURE = 3; // the file content signature, should not be hashed
    protected static final byte SIGNATURE_TYPE_FILE_HASH = 4; // next 48 bytes are SHA-384 of content of record file
    protected static final byte HASH_SIZE = 48; // the size of the hash

    @Override
    public FileStreamSignature read(InputStream inputStream) {
        FileStreamSignature fileStreamSignature = new FileStreamSignature();

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream))) {
            byte[] fileHash = new byte[HASH_SIZE];

            byte hashTypeDelimiter = dis.readByte();
            validate(SIGNATURE_TYPE_FILE_HASH, hashTypeDelimiter, "hashDelimiter");

            int length = dis.read(fileHash);
            validate(fileHash.length, length, "hashLength");

            fileStreamSignature.setFileHash(fileHash);

            byte signatureTypeDelimiter = dis.readByte();
            validate(SIGNATURE_TYPE_SIGNATURE, signatureTypeDelimiter, "signatureDelimiter");

            int sigLength = dis.readInt();
            byte[] sigBytes = new byte[sigLength];
            dis.readFully(sigBytes);
            fileStreamSignature.setFileHashSignature(sigBytes);

            if (dis.available() != 0) {
                throw new SignatureFileParsingException("Extra data discovered in signature file");
            }

            fileStreamSignature.setSignatureType(SignatureType.SHA_384_WITH_RSA);

            return fileStreamSignature;
        } catch (IOException e) {
            throw new SignatureFileParsingException(e);
        }
    }
}
