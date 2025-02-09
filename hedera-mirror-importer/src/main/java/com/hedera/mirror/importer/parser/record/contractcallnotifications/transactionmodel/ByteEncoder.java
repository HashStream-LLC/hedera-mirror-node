package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.google.protobuf.ByteString;

import java.util.Base64;

public class ByteEncoder {
    public static String toBase64String(ByteString byteString) {
        return Base64.getEncoder().encodeToString(byteString.toByteArray());
    }
}
