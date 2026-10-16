package cz.ecis.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import cz.ecis.core.EcisContext;
import cz.ecis.core.exception.EcisRuntimeException;
import cz.ecis.core.exception.IllegalRecordStateException;
import cz.ecis.core.mail.MailService;
import cz.ecis.core.security.EcisUserDetails;
import cz.ecis.core.security.EcisUserDetailsService;
import cz.ecis.core.security.jwt.JwtService;
import cz.ecis.core.security.jwt.JwtService.JwtType;
import cz.ecis.core.security.password.EcisPasswordValidator;
import cz.ecis.core.settings.EcisAppSettings;
import cz.ecis.core.token.TokenGeneratorService;
import cz.ecis.core.token.TokenGeneratorService.TokenGeneratorResult;
import cz.ecis.db.ent.LoginAttemptEnt;
import cz.ecis.db.ent.PasswordResetEnt;
import cz.ecis.db.ent.RefreshTokenEnt;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.repo.PasswordResetRepository;
import cz.ecis.db.repo.RefreshTokenRepository;
import cz.ecis.db.repo.UserRepository;
import cz.ecis.localization.EcisLocaleContext;
import cz.ecis.mapper.SecMapper;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import cz.ecis.model.dto.PasswordResetDto;
import cz.ecis.model.dto.RefreshTokenRequestDto;
import cz.ecis.model.dto.RequestPasswordResetDto;
import cz.ecis.model.dto.TwoFaCodeDto;
import cz.ecis.model.dto.TwoFaQrCodeDto;
import cz.ecis.model.dto.UserCredentialsDto;
import cz.ecis.utils.SecurityUtils;
import dev.samstevens.totp.time.SystemTimeProvider;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final EcisAppSettings ecisAppSettings;

    private final EcisPasswordValidator ecisPasswordValidator;

    private final PasswordEncoder passwordEncoder;
    private final AccountStatusUserDetailsChecker userDetailsChecker;
    private final EcisUserDetailsService userDetailsService;
    private final LoginService loginService;
    private final JwtService jwtService;
    private final MailService mailService;
    private final TokenGeneratorService tokenGeneratorService;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;

    private final SecMapper secMapper;

    public LoginResponseDto getLoginResponse(LoginRequestDto request) {

        String ipAddress = EcisContext.getRequest().getEntry().getIpAddress();
        String username = request.getUsername();

        EcisContext.getRequest().getEntry().setUsername(username);

        LoginAttemptEnt attempt = this.loginService.onLogin(ipAddress);

        UserEnt user = null;

        try {
            EcisUserDetails eud = this.userDetailsService.loadUserByUsername(username, false);

            user = eud.getEnt();

            if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Bad credentials");
            }

            this.userDetailsChecker.check(eud);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            this.loginService.unknownUser(ipAddress, username);
            this.loginService.invalidLogin(attempt);
            throw e;
        } catch (LockedException | DisabledException | AccountExpiredException | CredentialsExpiredException e) {
            this.loginService.invalidLogin(attempt);
            throw e;
        }

        this.loginService.checkPasswordExpiration(attempt, user, ipAddress);

        if (user.isTwoFaEnabled()) {
            Integer twoFaCode = request.getTwoFaCode();
            if (twoFaCode == null) {
                LoginResponseDto dto = new LoginResponseDto();
                dto.setTwoFaRequired(true);
                return dto;
            } else {
                if (user.getTwoFaSecret() == null || user.getTwoFaSecret().isBlank()) {
                    throw new AccessDeniedException("Error during validating 2FA code");
                } else if (
                    new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider()).isValidCode(user.getTwoFaSecret(), twoFaCode.toString())
                ) {
                    return this.loginService.getSuccessLoginResponse(attempt, ipAddress, user);
                } else {
                    this.loginService.invalidLogin(attempt);
                    throw new AccessDeniedException("2FA code is invalid");
                }
            }
        }

        return this.loginService.getSuccessLoginResponse(attempt, ipAddress, user);
    }

    public UserCredentialsDto getCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            UserCredentialsDto dto = new UserCredentialsDto();
            dto.setUsername("anonymous");
            return dto;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof EcisUserDetails user) {
            return this.secMapper.userToCredentialsDto(user.getEnt());

        } else {
            UserCredentialsDto dto = new UserCredentialsDto();
            dto.setUsername("anonymous");
            return dto;
        }
    }

    public List<String> getRoles() {
        return SecurityUtils.getUserRoles();
    }

    @Transactional
    public void logout() {
        String sessionId = EcisContext.getRequest().getEntry().getSessionId();
        this.refreshTokenRepository.findBySessionIdAndRevokedAtIsNull(sessionId).ifPresent(item -> {
            item.setRevokedAt(OffsetDateTime.now(ZoneId.systemDefault()));
            item.setRevokedByLogout(true);
            LOGGER.debug(String.format("Session %s found and will be revoked", sessionId));
            this.refreshTokenRepository.save(item);
            return;
        });
        LOGGER.warn(String.format("Session %s not found and will not be revoked", sessionId));
    }

    @Transactional
    public TwoFaQrCodeDto getTwoFaQ() {
        UserEnt user = SecurityUtils.getUserEnt();

        if (user.isTwoFaEnabled() && StringUtils.isBlank(user.getTwoFaSecret())) {
            throw new IllegalRecordStateException("User is connected with authenticator, please remove it first");
        }

        SecretGenerator secretGenerator = new DefaultSecretGenerator();

        String secret = secretGenerator.generate();


        QrData data = new QrData.Builder()
            .label("ECIS - " + user.getFullname())
            .secret(secret)
            .issuer("ECIS")
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
        .build();
        
        TwoFaQrCodeDto result = new TwoFaQrCodeDto();
        result.setQrUrl(data.getUri());

        user.setTwoFaSecret(secret);
        this.userRepository.save(user);

        return result;
    }

    @Transactional
    public void enableTwoFaQ(TwoFaCodeDto dto) {
        UserEnt user = SecurityUtils.getUserEnt();

        if (user.isTwoFaEnabled()) {
            throw new IllegalRecordStateException("2fa is already enabled for this user");
        }

        if (
            dto.getTwoFaCode() == null ||
            !new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider()).isValidCode(user.getTwoFaSecret(), dto.getTwoFaCode().toString())
        ) {
            throw new IllegalRecordStateException("Chyba při ověření kódu, zkuste to prosím znovu");
        }

        user.setTwoFaEnabled(true);
        this.userRepository.save(user);
    }

    @Transactional
    public LoginResponseDto refreshToken(RefreshTokenRequestDto dto) {
        EcisContext.getRequest().getEntry().setRefresh(true);
        if (StringUtils.isBlank(dto.getRefreshToken())) {
            throw new IllegalRecordStateException("Refresh token is required");
        }

        try {
            this.jwtService.extractClaims(dto.getRefreshToken(), JwtType.REFRESH);
        } catch (Exception e) {
            LOGGER.warn("Refresh token is invalid: {}", e.getMessage());
            throw new IllegalRecordStateException("Refresh token is invalid");
        }

        RefreshTokenEnt refreshToken = this.refreshTokenRepository.findBySessionIdAndRevokedAtIsNull(
            EcisContext.getRequest().getEntry().getSessionId()
        ).orElseThrow(() -> {
            LOGGER.warn("Refresh token not found or is already revoked for session {}", EcisContext.getRequest().getEntry().getSessionId());
            return new IllegalRecordStateException("Refresh token is invalid");
        });

        if (!refreshToken.getUserId().equals(SecurityUtils.getUserId())) {
            LOGGER.warn("Refresh token user id {} does not match current user id {}", refreshToken.getUserId(), SecurityUtils.getUserId());
            throw new IllegalRecordStateException("Refresh token is invalid");
        }

        refreshToken.setRevokedAt(OffsetDateTime.now(ZoneId.systemDefault()));
        this.refreshTokenRepository.save(refreshToken);

        LoginResponseDto result = new LoginResponseDto();
        result.setToken(this.jwtService.generateToken(SecurityUtils.getUserEnt(), JwtType.ACCESS, UUID.randomUUID().toString()));
        result.setTwoFaRequired(false);
        return result;
    }

    public void requestPasswordReset(RequestPasswordResetDto dto) {
        if (StringUtils.isEmpty(dto.getEmail())) {
            return;
        }

        Optional<UserEnt> userOpt = this.userRepository.findByEmailIgnoreCase(dto.getEmail());
        if (userOpt.isEmpty()) {
            this.passwordResetHoneypot();
            return;
        }
        UserEnt user = userOpt.get();
        Integer minutesValidity = this.ecisAppSettings.password().resetMinutesValid();
        Integer waitMinutes = ecisAppSettings.password().resetMinutesNextWait();

        PasswordResetEnt ent = this.passwordResetRepository.findFirstByUserIdOrderByGeneratedAtDesc(user.getId())
        .orElse(null);

        TokenGeneratorResult token;
        if (ent == null) {
            token = this.tokenGeneratorService.generateRandomSha256();
            ent = this.passwordResetRepository.save(
                this.secMapper.createPasswordReset(user, token.hash(), minutesValidity)
            );
        } else if (ent.getLastRequestedAt().plusMinutes(ecisAppSettings.password().resetMinutesNextWait()).isAfter(OffsetDateTime.now())) {
            LOGGER.warn("Password reset email skipped due to cooldown. Minimum interval: {} minutes.", waitMinutes);
            this.passwordResetHoneypot();
            return;
        } else {
            // if (ent.getRevokedAt() != null) {
            //     LOGGER.warn("Password reset is revoked already");
            //     this.passwordResetHoneypot();
            //     return;
            // }
            token = this.tokenGeneratorService.generateRandomSha256();
            this.secMapper.updateUsePasswordReset(ent, token.hash(), minutesValidity);
            ent = this.passwordResetRepository.save(ent);
        }
      
        String resetUrl = String.format("%s?token=%s&email=%s", this.ecisAppSettings.password().resetUrl(), token.raw(), user.getEmail());
        Context context = new Context(EcisLocaleContext.getLocale());
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("expirationMinutes", minutesValidity);
        try {
            this.mailService.sendRender(user.getEmail(), "email.password-reset.subject", "password-reset", context);
        } catch (MessagingException e) {
            LOGGER.error("Error during sending email to {}", user.getEmail(), e);
        }
    }

    @Transactional
    public void resetPassword(PasswordResetDto dto) {
        if (StringUtils.isAnyEmpty(dto.getToken(), dto.getEmail(), dto.getPassword())) {
            LOGGER.warn("One of required properties is empty. Token: {}, Email: {}, PasswordIsNull: {}", dto.getToken(), dto.getEmail(), dto.getPassword() == null);
            this.passwordResetHoneypot();
            return;
        }

        if (!this.ecisPasswordValidator.validatePassword(dto.getPassword())) {
            throw new EcisRuntimeException("Password reset token is expired", HttpStatus.BAD_REQUEST);
        }
        
        String tokenHash = DigestUtils.sha256Hex(dto.getToken());

        System.out.println(tokenHash);
        System.out.println(dto.getToken());

        Optional<PasswordResetEnt> passwordOpt = this.passwordResetRepository.findFirstByUserEmailAndTokenHash(dto.getEmail(), tokenHash);
        if (passwordOpt.isEmpty()) {
            LOGGER.warn("Password reset entry not found");
            this.passwordResetHoneypot();
            return;
        }
        PasswordResetEnt passwordReset = passwordOpt.get();
        if (passwordReset.getRevokedAt() != null) {
            LOGGER.warn("Password reset is revoked already");
            this.passwordResetHoneypot();
            return;
        }


        OffsetDateTime now = OffsetDateTime.now();
        if (now.isAfter(passwordReset.getValidTo())) {
            LOGGER.warn("Password reset token is expired.");
            throw new EcisRuntimeException("Password reset token is expired", HttpStatus.BAD_REQUEST);
        }
        passwordReset.setRevokedAt(now);
        this.passwordResetRepository.save(passwordReset);

        UserEnt user = passwordReset.getUser();
        user.updatePassword(this.passwordEncoder.encode(dto.getPassword()));
        this.userRepository.save(user);
    }

    private void passwordResetHoneypot() {
        try {
            LOGGER.warn("Honeypot for requestPasswordResetHoneypot triggered :)");
            Thread.sleep(90);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Honeypot for requestPasswordResetHoneypot was intterupted :(");
        }
    }
}
