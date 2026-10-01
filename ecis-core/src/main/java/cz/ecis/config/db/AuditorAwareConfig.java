package cz.ecis.config.db;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import cz.ecis.core.audit.IEcisRevisionResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "dateTimeProvider")
@RequiredArgsConstructor
public class AuditorAwareConfig {
	
	private final IEcisRevisionResolver resolver;

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            return Optional.of(this.resolver.resolveUsername());
        };
    }

    @Bean
    DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(
            OffsetDateTime.now(ZoneId.systemDefault())
        );
    }
}
