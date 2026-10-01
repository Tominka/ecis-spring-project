package cz.ecis.core.settings;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ecis.app")
public record EcisAppSettings(
    @Length(max = 255) String displayAppName
) {}