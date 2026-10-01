package cz.ecis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.db.ent.ParentEnt;
import cz.ecis.model.dto.ParentDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface ParentMapper {

    public ParentDto toDto(ParentEnt ent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "camp", ignore = true)
    // @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    public void update(@MappingTarget ParentEnt ent, ParentDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "camp", ignore = true)
    // @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    public ParentEnt create(ParentDto dto);
}
