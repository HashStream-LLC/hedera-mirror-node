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

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.common.primitives.Bytes;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.junit.jupiter.api.DynamicTest;

import com.hedera.mirror.importer.exception.SignatureFileParsingException;

abstract class AbstractSignatureFileReaderTest {

    protected InputStream getInputStream(File file) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    protected InputStream getInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    //Dynamically generate tests for corrupt/invalid signature file tests
    protected Iterable<DynamicTest> generateCorruptedFileTests(SignatureFileReader fileReader,
                                                               List<SignatureFileSection> signatureFileSections) {
        List<DynamicTest> testCases = new ArrayList<>();

        //Add a test for an empty stream
        testCases.add(DynamicTest.dynamicTest(
                "blankFile",
                () -> {
                    InputStream blankInputStream = getInputStream(new byte[0]);
                    SignatureFileParsingException e = assertThrows(SignatureFileParsingException.class,
                            () -> {
                                fileReader.read(blankInputStream);
                            });
                    assertTrue(e.getMessage().contains("EOFException"));
                }));

        byte[] validSignatureBytes = new byte[0];
        for (int i = 0; i < signatureFileSections.size(); i++) {
            //Add new valid section of the signature file to the array
            validSignatureBytes = i > 0 ? Bytes.concat(validSignatureBytes, signatureFileSections.get(i - 1)
                    .getValidDataBytes()) : validSignatureBytes;

            SignatureFileSection sectionToCorrupt = signatureFileSections.get(i);

            //Some sections are not validated by the reader and don't need a test
            if (sectionToCorrupt.getCorruptTestName() == null) {
                continue;
            }

            //Add the corrupted section of the signature file to the valid sections
            byte[] fullSignatureBytes = Bytes.concat(validSignatureBytes, sectionToCorrupt.getCorruptBytes());

            //Create a test that checks that an exception was thrown, and the message matches.
            testCases.add(DynamicTest.dynamicTest(
                    signatureFileSections.get(i).getCorruptTestName(),
                    () -> {
                        InputStream corruptInputStream = getInputStream(fullSignatureBytes);
                        SignatureFileParsingException e = assertThrows(SignatureFileParsingException.class,
                                () -> {
                                    fileReader.read(corruptInputStream);
                                });
                        sectionToCorrupt.validateError(e.getMessage());
                    }));
        }
        return testCases;
    }

    protected static final SignatureFileSectionCorrupter incrementLastByte = (bytes -> {
        byte[] corruptBytes = Arrays.copyOf(bytes, bytes.length);
        corruptBytes[corruptBytes.length - 1] = (byte) (corruptBytes[corruptBytes.length - 1] + 1);
        return corruptBytes;
    });

    protected static final SignatureFileSectionCorrupter truncateLastByte = (bytes -> Arrays
            .copyOfRange(bytes, 0, bytes.length - 1));

    @Value
    @AllArgsConstructor
    protected class SignatureFileSection {
        private final byte[] validDataBytes;
        private final String corruptTestName;
        private final SignatureFileSectionCorrupter byteCorrupter;
        private final String invalidExceptionMessage;
        @Getter(lazy = true)
        private final byte[] corruptBytes = byteCorrupter.corruptBytes(validDataBytes);

        public void validateError(String errorMessage) {
            assertTrue(errorMessage.contains(invalidExceptionMessage));
        }
    }

    protected interface SignatureFileSectionCorrupter {
        byte[] corruptBytes(byte[] bytes);
    }
}
