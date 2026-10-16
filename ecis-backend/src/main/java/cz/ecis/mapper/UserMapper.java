package cz.ecis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.db.ent.UserSettingsEnt;
import cz.ecis.model.dto.UserSettingsDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface UserMapper {

    @Mapping(source = "camp.id", target = "selectedCampId")
    UserSettingsDto userSetingsToDto(UserSettingsEnt ent);

    @Mapping(target = "camp", ignore = true)
    void updateUserSettings(@MappingTarget UserSettingsEnt ent, UserSettingsDto dto);

}
