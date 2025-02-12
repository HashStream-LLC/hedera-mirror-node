package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

/** Keeping parity with integer statuses from Sentinel */
public class NotificationEventStatus {
    // Not all statuses are used by the importer, but including them here for context
    public static final int Success = 0;
    public static final int Failure = 1;
    public static final int Pending = 2;
    public static final int Skipped = 3;
}
