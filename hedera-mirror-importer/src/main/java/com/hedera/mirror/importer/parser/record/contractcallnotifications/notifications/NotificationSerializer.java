package com.hedera.mirror.importer.parser.record.contractcallnotifications.notifications;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.hedera.mirror.common.converter.EntityIdDeserializer;
import com.hedera.mirror.common.converter.EntityIdSerializer;
import com.hedera.mirror.common.domain.entity.EntityId;

import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S6548")
public class NotificationSerializer extends JsonSerializer<Object> {

    public static final NotificationSerializer Instance = new NotificationSerializer();
    public static final ObjectMapper NotificationObjectMapper;

    static {
        var module = new SimpleModule();
        module.addDeserializer(EntityId.class, EntityIdDeserializer.INSTANCE);
        module.addSerializer(EntityId.class, EntityIdSerializer.INSTANCE);

        NotificationObjectMapper = new ObjectMapper()
                .registerModule(module)
                .registerModule(new Jdk8Module())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static void init() {
        // Called by other classes to ensure the static initializer runs
    }

    @Override
    public void serialize(Object o, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var json = NotificationObjectMapper.writeValueAsString(o);
        gen.writeString(json);
    }
}
