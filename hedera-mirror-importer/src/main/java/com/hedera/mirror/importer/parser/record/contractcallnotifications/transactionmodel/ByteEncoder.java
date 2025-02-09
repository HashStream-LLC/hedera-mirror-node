package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;

import java.util.Base64;

public class ByteEncoder {
    public static String toBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String toBase64String(ByteString byteString) {
        return toBase64String(byteString.toByteArray());
    }

    public static String toBase64String(BytesValue bytesValue) {
        return toBase64String(bytesValue.toByteArray());
    }
}
