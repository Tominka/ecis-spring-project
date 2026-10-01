package cz.ecis.core.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "ecis.login")
@Validated
public record EcisLoginSettings(

    @NotNull Boolean enableAttemptProtection,
    @NotNull Boolean enablePasswordExpiration,
    @NotNull @Min(1) Integer passwordExpirationTime,
    @NotNull @Min(1) Integer maxAttempts,
    @NotNull @Min(1) Integer blockTime
) {}
