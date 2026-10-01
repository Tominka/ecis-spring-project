package cz.ecis.config.mapper;

import org.mapstruct.MapperConfig;

@MapperConfig(componentModel = "spring")
public class DefaultMapperConfig {

    String mapString(String value) {
        return value == null || value.isEmpty() ? null : value.trim();
    }

}
