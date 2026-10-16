package cz.ecis.core.settings;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "ecis.app")
public record EcisAppSettings(
    @Length(max = 255) String displayAppName,
    Password password
) {
    public record Password(
        @NotNull String resetUrl,
        @NotNull @Min(1) Integer resetMinutesValid,
        Integer resetMinutesNextWait,
        @NotNull @Min(6) @Max(255) Integer minLength
    ) {}
}