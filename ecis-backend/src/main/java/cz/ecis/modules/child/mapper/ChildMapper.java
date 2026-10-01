package cz.ecis.modules.child.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.db.ent.ChildEnt;
import cz.ecis.modules.child.model.ChildDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface ChildMapper {

    public ChildDto toDto(ChildEnt ent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    public void update(@MappingTarget ChildEnt ent, ChildDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    public ChildEnt create(ChildDto dto);
}
