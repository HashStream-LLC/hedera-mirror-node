package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Batching {

    /// Split the given stream of items into different batches of the given batch size
    public static <T> List<List<T>> batchItems(Stream<T> stream, int batchSize) {
        AtomicInteger counter = new AtomicInteger(0);
        return new ArrayList<>(stream
                .collect(Collectors.groupingBy(i -> counter.getAndIncrement() / batchSize))
                .values());
    }
}
