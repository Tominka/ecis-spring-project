package cz.ecis.core.settings;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    EcisAuditSettings.class
})
public class SettingsConfig {
    
}
