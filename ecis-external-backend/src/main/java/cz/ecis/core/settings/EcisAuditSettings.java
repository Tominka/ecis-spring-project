package cz.ecis.core.settings;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "ecis.audit")
@Validated
public record EcisAuditSettings(
    @NotNull @Length(min = 3, max = 255) String defaultAuditor
) {}
