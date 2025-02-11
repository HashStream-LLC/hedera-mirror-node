package com.hedera.mirror.importer.parser.record.contractcallnotifications.transactionmodel;

import com.hedera.mirror.common.domain.entity.EntityId;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class OptionalHandler {
    public static <TInput, TOutput> Optional<TOutput> mapIfPopulated(
            TInput input, Predicate<TInput> hasValue, Function<TInput, TOutput> mapper
    ) {
        return hasValue.test(input) ? Optional.of(mapper.apply(input)) : Optional.empty();
    }

    public static <TInput> Optional<String> mapEntityIdStringIfPopulated(
            TInput input, Predicate<TInput> hasValue, Function<TInput, EntityId> entityIdMapper
    ) {
        Optional<EntityId> entityId = mapIfPopulated(
                input,
                hasValue,
                entityIdMapper
        );
        return entityId.map(EntityId::toString);
    }
}
