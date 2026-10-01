package cz.ecis.core;

import java.util.Set;

import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cz.ecis.core.environment.EnvTypeEnum;
import cz.ecis.core.settings.EcisAppSettings;
import lombok.Getter;

@Component
public class EnvironmentContext {

    private final Environment environment;

    @Getter
    private final EnvironmentAppContext appContext;

    public EnvironmentContext(
        Environment environment,
        EcisAppSettings ecisAppSettings,
        BuildProperties buildProperties
    ) {
        this.environment = environment;

        this.appContext = new EnvironmentAppContext(
            buildProperties.getVersion(), this.getEnvType(), ecisAppSettings.displayAppName()
        );
    }

    private EnvTypeEnum getEnvType() {
        Set<String> profiles = Set.of(this.environment.getActiveProfiles());

        if (environment.matchesProfiles("prod")) {
            return EnvTypeEnum.PROD;
        }

        if (environment.matchesProfiles("test")) {
            return EnvTypeEnum.TEST;
        }

        if (environment.matchesProfiles("dev")) {
            return EnvTypeEnum.DEV;
        }

        throw new IllegalStateException(
            "Unknown Spring profile: " + profiles
        );
    }

    public record EnvironmentAppContext(String version, EnvTypeEnum type, String displayName) {}
}
