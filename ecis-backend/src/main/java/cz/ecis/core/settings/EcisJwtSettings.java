package cz.ecis.core.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "ecis.jwt")
@Validated
public record EcisJwtSettings(
    Access access,
    Refresh refresh
) {

    public record Access(
        @NotNull String secretKey,
        @NotNull @Min(1) Integer validity
    ) {}

    public record Refresh(
        @NotNull String secretKey,
        @NotNull @Min(1) Integer validity
    ) {}
}