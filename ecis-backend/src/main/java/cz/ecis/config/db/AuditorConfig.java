package cz.ecis.config.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import cz.ecis.core.audit.IEcisRevisionResolver;
import cz.ecis.core.settings.EcisAuditSettings;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuditorConfig {
	
	 private final EcisAuditSettings ecisAuditSettings;

	@Bean
	IEcisRevisionResolver revisionResolver() {
		return new IEcisRevisionResolver() {

	        @Override
	        public String resolveUsername() {
	            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	            if (auth != null && auth.isAuthenticated()
	                    && !(auth instanceof AnonymousAuthenticationToken)) {
	                return auth.getName();
	            }

	            return ecisAuditSettings.defaultAuditor();
	        }
	        
	        @Override
	        public String resolveApplication() {
	            return "APP";
	        }

	    };
	}
}
