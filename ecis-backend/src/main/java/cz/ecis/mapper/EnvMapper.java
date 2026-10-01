package cz.ecis.mapper;

import org.mapstruct.Mapper;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.core.EnvironmentContext.EnvironmentAppContext;
import cz.ecis.model.dto.EnvDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface EnvMapper {
    
    EnvDto mapContextToDto(EnvironmentAppContext appContext);
}
