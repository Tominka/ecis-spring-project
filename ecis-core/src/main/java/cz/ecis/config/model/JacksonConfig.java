package cz.ecis.config.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
// import tools.jackson.module.afterburner.AfterburnerModule;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Bean
    public ObjectMapper objectMapper() {

        SimpleModule javaTimeModule = new SimpleModule();

        javaTimeModule.addSerializer(OffsetDateTime.class, new ValueSerializer<OffsetDateTime>() {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator gen, SerializationContext ctxt) {
                gen.writeString(OFFSET_DATETIME_FORMATTER.format(value));
            }
        });

        return JsonMapper.builder()
            // .addModule(new AfterburnerModule())
            .addModule(javaTimeModule)
            .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    }
}