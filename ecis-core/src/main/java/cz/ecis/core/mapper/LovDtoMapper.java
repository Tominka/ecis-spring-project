package cz.ecis.core.mapper;

import org.mapstruct.Mapper;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.model.LovDto;

@Mapper(componentModel = "spring")
public interface LovDtoMapper {

    public LovDto toLovDto(ILovEntity ent);

}
