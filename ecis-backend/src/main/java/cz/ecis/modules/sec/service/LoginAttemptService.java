package cz.ecis.modules.sec.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cz.ecis.core.exception.AccountBlockedException;
import cz.ecis.core.settings.EcisLoginSettings;
import cz.ecis.db.ent.LoginAttemptEnt;
import cz.ecis.db.repo.LoginAttemptRepository;
import cz.ecis.modules.sec.mapper.LoginMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final EcisLoginSettings ecisLoginSettings;
    private final LoginAttemptRepository loginAttemptRepository;
    private final LoginMapper loginMapper;

    @Transactional(dontRollbackOn = AccountBlockedException.class)
    public LoginAttemptEnt onLogin(String ipAddress) {

        Integer maxAttempts = this.ecisLoginSettings.maxAttempts();

        if (Boolean.FALSE.equals(this.ecisLoginSettings.enableAttemptProtection())) {
            // return this.loginAttemptRepository.save(this.loginMapper.createLoginAttempt(ipAddress, maxAttempts));
            return null;
        }

        Optional<LoginAttemptEnt> attemptOpt = this.loginAttemptRepository.findFirstByIpAddressAndHistory(ipAddress, false);

        if (attemptOpt.isPresent()) {
            LoginAttemptEnt attempt = attemptOpt.get();
            if (this.isBlocked(attempt)) {
                attempt.onAttempt();
                this.loginAttemptRepository.save(attempt);
                throw new AccountBlockedException("Přístup byl dočasně zablokován.");
            } else if (this.attempsExceeded(attempt)) {
                attempt.setUnblockedCount(attempt.getUnblockedCount()+1);
                attempt.setAttemptsCurrent(0);
                return this.loginAttemptRepository.save(attempt);
            } else {
                return attempt;
            }
        } else {
            return this.loginAttemptRepository.save(this.loginMapper.createLoginAttempt(ipAddress, maxAttempts));
        }
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

    @Transactional(dontRollbackOn = AccountBlockedException.class)
    public LoginAttemptEnt invalidLogin(LoginAttemptEnt attempt) {

        if (!this.ecisLoginSettings.enableAttemptProtection() || attempt == null) {
            return null;
        }

        attempt.onAttempt();

        this.loginAttemptRepository.save(attempt);

        Integer maxAttempts = this.ecisLoginSettings.maxAttempts();

        if (attempt.getAttemptsCurrent() > maxAttempts) { // Ten poslední pokus si uživatel zaslouží :)
            throw new AccountBlockedException("Přístup byl dočasně zablokován.");
        }

        return attempt;

    }

}
