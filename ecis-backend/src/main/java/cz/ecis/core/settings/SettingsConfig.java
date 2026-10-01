package cz.ecis.core.settings;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    EcisLoginSettings.class, EcisJwtSettings.class, EcisAuditSettings.class,
    EcisAppSettings.class
})
public class SettingsConfig {
    
}
