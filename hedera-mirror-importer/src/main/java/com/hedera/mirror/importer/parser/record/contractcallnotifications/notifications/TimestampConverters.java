package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.hederahashgraph.api.proto.java.Timestamp;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimestampConverters {
    public static Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static ZonedDateTime toZonedDatetime(Timestamp timestamp) {
        Instant instant = toInstant(timestamp);
        return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
