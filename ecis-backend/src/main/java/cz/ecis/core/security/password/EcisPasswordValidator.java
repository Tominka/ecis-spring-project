package cz.ecis.core.security.password;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cz.ecis.core.settings.EcisAppSettings;
import cz.ecis.db.ent.UserEnt;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EcisPasswordValidator {

    public static final Logger LOGGER = LogManager.getLogger();
    
    private final EcisAppSettings ecisAppSettings;

    private final PasswordEncoder passwordEncoder;

    public boolean validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            LOGGER.warn("Password is empty");
            return false;
        }
        if (password.length() <= this.ecisAppSettings.password().minLength()) {
            LOGGER.warn("Password didn´t meet minimal requirements");
            return false;
        }
        return true;
    }

    public boolean passwordMatches(String rawPassword, UserEnt user) {
        if (user == null || StringUtils.isBlank(rawPassword)) {
            return false;
        }

        return this.passwordEncoder.matches(rawPassword, user.getPassword());
    }

}
