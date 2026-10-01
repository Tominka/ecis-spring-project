package cz.ecis.modules.camp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.db.ent.CampEnt;
import cz.ecis.modules.camp.model.CampDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface CampMapper {

    public CampDto toDto(CampEnt ent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    public void update(@MappingTarget CampEnt ent, CampDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    public CampEnt create(CampDto dto);
}
