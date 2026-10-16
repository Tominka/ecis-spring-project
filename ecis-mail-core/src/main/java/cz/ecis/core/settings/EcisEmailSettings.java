package cz.ecis.core.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ecis.mail")
@Validated
public record EcisEmailSettings(
    String host,
    Integer port,
    String username,
    String password,
    String from
) {}