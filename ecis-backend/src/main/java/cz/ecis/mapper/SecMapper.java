package cz.ecis.mapper;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.core.EcisContext;
import cz.ecis.db.ent.LogEntryEnt;
import cz.ecis.db.ent.PasswordResetEnt;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.ent.UserSettingsEnt;
import cz.ecis.model.dto.UserCredentialsDto;
import cz.ecis.model.dto.UserSettingsDto;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface SecMapper {

    @Mapping(source = "systemAdmin", target = "isSystemAdmin")
    @Mapping(source = "twoFaEnabled", target = "is2FaEnabled")
    UserCredentialsDto userToCredentialsDto(UserEnt user);

    @Mapping(source = "camp.id", target = "selectedCampId")
    UserSettingsDto userSetingsToDto(UserSettingsEnt ent);

    default PasswordResetEnt createPasswordReset(UserEnt user, String tokenHash, Integer minutesValid) {
        OffsetDateTime now = OffsetDateTime.now();
        PasswordResetEnt ent = new PasswordResetEnt();
        ent.setUser(user);
        ent.setTokenHash(tokenHash);
        ent.setValidTo(now.plusMinutes(minutesValid));
        ent.setGeneratedAt(now);
        ent.setLastRequestedAt(now);
        
        LogEntryEnt entry = EcisContext.getRequest().getEntry();

        ent.setIpAddress(entry.getIpAddress());
        ent.setUserAgent(entry.getUserAgent());

        return ent;
    }

    default void updateUsePasswordReset(PasswordResetEnt ent, String tokenHash, Integer minutesValid) {
        OffsetDateTime now = OffsetDateTime.now();
        ent.setLastRequestedAt(now);
        ent.setRevokedAt(null);
        ent.setValidTo(now.plusMinutes(minutesValid));
        ent.setTokenHash(tokenHash);
    }

}
