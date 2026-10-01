package cz.ecis.modules.sec.mapper;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.mapstruct.Mapper;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.core.EcisContext;
import cz.ecis.db.ent.LogLoginEnt;
import cz.ecis.db.ent.LoginAttemptEnt;
import cz.ecis.db.ent.RefreshTokenEnt;
import cz.ecis.db.ent.UserEnt;

@Mapper(componentModel = "spring", config = DefaultMapperConfig.class)
public interface LoginMapper {
    
    default LoginAttemptEnt createLoginAttempt(String ipAddress, Integer maxAttempts) {
        LoginAttemptEnt attempt = new LoginAttemptEnt();
        attempt.setIpAddress(ipAddress);
        attempt.setAttemptsMax(maxAttempts);
        attempt.setAttemptsTotal(0);
        attempt.setAttemptsCurrent(0);
        attempt.setLastAttempt(OffsetDateTime.now(ZoneId.systemDefault()));
        attempt.setUnblockedCount(0);
        attempt.setUnblockedCountUser(0);
        attempt.setHistory(false);
        return attempt;
    }

    default LogLoginEnt createLogLogin(String ipAddress, String username, boolean authorized, boolean passwordExpired) {
        LogLoginEnt ent = new LogLoginEnt();
        ent.setIpAddress(ipAddress);
        ent.setUsername(username);
        ent.setAuthorized(authorized);
        ent.setPasswordExpired(passwordExpired);
        ent.setLoggedInAt(OffsetDateTime.now(ZoneId.systemDefault()));
        ent.setCreatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
        return ent;
    }

    default RefreshTokenEnt createRefreshTokenEnt(UserEnt user, String sessionId, Integer validitySeconds) {
        RefreshTokenEnt ent = new RefreshTokenEnt();
        ent.setUserId(user.getId());
        ent.setSessionId(sessionId);
        ent.setIpAddress(EcisContext.getRequest().getEntry().getIpAddress());
        ent.setUserAgent(EcisContext.getRequest().getEntry().getUserAgent());
        // ent.setTokenHash("X");
        ent.setExpiresAt(OffsetDateTime.now(ZoneId.systemDefault()).plus(validitySeconds, ChronoUnit.SECONDS));
        ent.setRevokedByLogout(false);
        ent.setCreatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
        return ent;
    }
}
