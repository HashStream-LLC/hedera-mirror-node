package com.hedera.mirror.importer.parser.record.contractcallnotifications;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnProperty(
        name = "hedera.mirror.importer.parser.record.contractcallnotifications.enabled",
        havingValue = "true"
)
public @interface ConditionalOnContractCallNotificationsRecordParser {}
