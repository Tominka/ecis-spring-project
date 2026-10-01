package cz.ecis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.ent.UserSettingsEnt;
import cz.ecis.model.dto.UserCredentialsDto;
import cz.ecis.model.dto.UserSettingsDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface SecMapper {

    @Mapping(source = "systemAdmin", target = "isSystemAdmin")
    @Mapping(source = "twoFaEnabled", target = "is2FaEnabled")
    UserCredentialsDto userToCredentialsDto(UserEnt user);

    @Mapping(source = "campId", target = "selectedCampId")
    UserSettingsDto userSetingsToDto(UserSettingsEnt ent);

}
