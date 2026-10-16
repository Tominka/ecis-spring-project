package cz.ecis.service;

import cz.ecis.core.security.EcisUserDetailsService;
import cz.ecis.core.security.password.EcisPasswordValidator;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.repo.UserRepository;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cz.ecis.core.exception.EcisRuntimeException;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.mapper.LovDtoMapper;
import cz.ecis.core.model.LovDto;
import cz.ecis.mapper.CampMapper;
import cz.ecis.mapper.UserMapper;
import cz.ecis.model.dto.CampDto;
import cz.ecis.model.dto.UserCampChangeDto;
import cz.ecis.model.dto.UserPasswordChangeDto;
import cz.ecis.model.dto.UserSettingsDto;
import cz.ecis.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PasswordEncoder passwordEncoder;
    private final EcisPasswordValidator ecisPasswordValidator;
    private final EcisUserDetailsService ecisUserDetailsService;

    private final UserRepository userRepository;

    private final CampMapper campMapper;
    private final LovDtoMapper lovDtoMapper;
    private final UserMapper userMapper;

    public List<CampDto> getUserCamps() {
        return SecurityUtils.getUserCamps().stream()
            .map(this.campMapper::toDto)
            .toList();
    }

    public List<LovDto> getUserCampsLov() {
        return SecurityUtils.getUserCamps().stream()
            .map(this.lovDtoMapper::toLovDto)
            .toList();
    }

    @Transactional
    public void selectCamp(UserCampChangeDto dto) {
        this.ecisUserDetailsService.selectUserCamp(dto.getId());
    }

    @Transactional
    public UserSettingsDto updateUserSettings(Long userId, UserSettingsDto dto) {
        UserEnt currentUser = SecurityUtils.getUserEnt();
        if (userId.equals(currentUser.getId())) { // update only
            this.userMapper.updateUserSettings(currentUser.getSettings(), dto);
            return this.userMapper.userSetingsToDto(
                this.userRepository.save(currentUser).getSettings()
            );
        } else { // only system administrator for any user
            if (!currentUser.isSystemAdmin()) {
                LOGGER.warn("Restricted update for user settings detected. User is not system administrator.");
                throw new EntityNotExistsException(Map.of("id", userId));
            }
            UserEnt requestedUser = this.userRepository.findById(userId).orElseThrow(
                () -> new EntityNotExistsException(Map.of("id", userId))
            );

            this.userMapper.updateUserSettings(requestedUser.getSettings(), dto);
            return this.userMapper.userSetingsToDto(
                this.userRepository.save(requestedUser).getSettings()
            );
        }
    }

    @Transactional
    public void changeUserPassword(Long userId, UserPasswordChangeDto dto) {
        UserEnt currentUser = SecurityUtils.getUserEnt();
        if (userId.equals(currentUser.getId())) { // update only
            this.changePassword(currentUser, currentUser, dto);
        } else { // only system administrator for any user
            if (!currentUser.isSystemAdmin()) {
                LOGGER.warn("Restricted user password update detected. User is not system administrator.");
                throw new EntityNotExistsException(Map.of("id", userId));
            }
            UserEnt requestedUser = this.userRepository.findById(userId).orElseThrow(
                () -> new EntityNotExistsException(Map.of("id", userId))
            );
            this.changePassword(currentUser, requestedUser, dto);
        }
    }

    private void changePassword(UserEnt currentUser, UserEnt user, UserPasswordChangeDto dto) {
        if (!user.getUsername().equals(dto.getUsername())) {
            LOGGER.warn("User username doesn't match with typed");
            throw new EntityNotExistsException(Map.of("id", user.getId()));
        }
        if (currentUser.getId().equals(user.getId())) {
            if (StringUtils.isBlank(dto.getActualPassword())) {
                throw new EcisRuntimeException("Actual password can't be blank", HttpStatus.BAD_REQUEST);
            }
            if (!this.ecisPasswordValidator.passwordMatches(dto.getActualPassword(), user)) {
                throw new EcisRuntimeException("Password doesn´t match with current password", HttpStatus.BAD_REQUEST);
            }
        }
        if (!this.ecisPasswordValidator.validatePassword(dto.getPassword())) {
            throw new EcisRuntimeException("New password does not meet minimum requirements", HttpStatus.BAD_REQUEST);
        }
        user.updatePassword(passwordEncoder.encode(dto.getPassword()));
        this.userRepository.save(user);
    }
}