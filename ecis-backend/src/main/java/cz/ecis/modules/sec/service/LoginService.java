package cz.ecis.modules.sec.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;

import cz.ecis.core.EcisContext;
import cz.ecis.core.exception.AccountBlockedException;
import cz.ecis.core.security.jwt.JwtService;
import cz.ecis.core.security.jwt.JwtService.JwtType;
import cz.ecis.core.settings.EcisJwtSettings;
import cz.ecis.core.settings.EcisLoginSettings;
import cz.ecis.db.ent.LoginAttemptEnt;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.repo.LogLoginRepository;
import cz.ecis.db.repo.LoginAttemptRepository;
import cz.ecis.db.repo.RefreshTokenRepository;
import cz.ecis.modules.sec.mapper.LoginMapper;
import cz.ecis.modules.sec.model.LoginResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final EcisLoginSettings ecisLoginSettings;

    private final LoginAttemptRepository loginAttemptRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final LoginAttemptService loginAttemptService;
    private final JwtService jwtService;

    private final LogLoginRepository logLoginRepository;
    private final LoginMapper loginMapper;

    private final EcisJwtSettings ecisJwtSettings;

    @Transactional(dontRollbackOn = AccountBlockedException.class)
    public LoginAttemptEnt onLogin(String ipAddress) {
        return this.loginAttemptService.onLogin(ipAddress);
    }

    public boolean isBlocked(LoginAttemptEnt ent) {
        return this.attempsExceeded(ent) && ! this.timeExceeded(ent);
    }

    public boolean attempsExceeded(LoginAttemptEnt ent) {
        Integer maxAttempts = this.ecisLoginSettings.maxAttempts();

        return ent.getAttemptsCurrent() >= maxAttempts;

    }

    public boolean timeExceeded(LoginAttemptEnt ent) {
        if (ent.getLastAttempt() == null) {
            return false;
        }

        int blockTimeMinutes = this.ecisLoginSettings.blockTime();

        OffsetDateTime allowedTime = ent.getLastAttempt().plusMinutes(blockTimeMinutes);

        return OffsetDateTime.now(ZoneId.systemDefault()).isAfter(allowedTime);
    }

    protected LoginResponseDto getSuccessLoginResponse(LoginAttemptEnt attempt, String ipAddress, UserEnt user) {
        UUID sessionId = UUID.randomUUID();

        this.authorizedLogin(attempt, ipAddress, user.getUsername());

        this.saveRefreshToken(user, sessionId.toString());

        LoginResponseDto dto = new LoginResponseDto();
        dto.setToken(this.jwtService.generateToken(user, JwtType.ACCESS, sessionId.toString()));
        dto.setRefreshToken(this.jwtService.generateToken(user, JwtType.REFRESH, sessionId.toString()));
        dto.setTwoFaRequired(false);
        return dto;
    }

    @Transactional
    public void saveRefreshToken(UserEnt user, String sessionId) {
        this.refreshTokenRepository.save(
            this.loginMapper.createRefreshTokenEnt(user, sessionId, this.ecisJwtSettings.refresh().validity())
        );
    }

    @Transactional(dontRollbackOn = AccountBlockedException.class)
    public LoginAttemptEnt invalidLogin(LoginAttemptEnt attempt) {
        return this.loginAttemptService.invalidLogin(attempt);
    }

    @Transactional
    public void checkPasswordExpiration(LoginAttemptEnt attempt, UserEnt user, String ipAddress) {

        if (Boolean.TRUE.equals(this.ecisLoginSettings.enablePasswordExpiration())/*&& this.ecisLoginSettings.passwordExpirationTime() != null*/) {
            if (user.getPasswordChanged() == null) {
                return;
            }
            
            if (
                user.getPasswordChanged().isAfter(OffsetDateTime.now(ZoneId.systemDefault()).minusDays(this.ecisLoginSettings.passwordExpirationTime()))
            ) {
                this.invalidLogin(attempt);
                this.logLoginRepository.save(this.loginMapper.createLogLogin(ipAddress, user.getUsername(), false, true));
                EcisContext.getRequest().getEntry().setPasswordExpired(true);
                throw new CredentialsExpiredException("Heslo vypršelo.");
            }
        }
        // else if (Boolean.TRUE.equals(this.passwordCheckEnabled)) {
        //     LOGGER.warn("Password check is enabled, but password validity days is not set. This may lead to unexpected behavior. Please check the configuration. Password expiration check is disabled.");
        // }
    }

    @Transactional
    public void unknownUser(String ipAddress, String username) {
        this.logLoginRepository.save(this.loginMapper.createLogLogin(ipAddress, username, false, false));
    }

    @Transactional
    public void authorizedLogin(LoginAttemptEnt attempt, String ipAddress, String username) {
        EcisContext.getRequest().getEntry().setAuthenticated(true);

        if (attempt != null) {
            attempt.setHistory(true);
            this.loginAttemptRepository.save(attempt);
        }

    }

}
