package cz.ecis.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import cz.ecis.core.RequestLoggingFilter;
import cz.ecis.core.handler.LoginExceptionHandler;
import cz.ecis.core.security.ApiKeyAuthenticationProvider;
import cz.ecis.core.security.ApiKeyFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final LoginExceptionHandler loginExceptionHandler;
    private final RequestLoggingFilter requestLoggingFilter;

    private static final List<String> PUBLIC_PATHS = List.of(
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/favicon.ico"
    );

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) {
        return http
            .csrf(csrfs -> csrfs.disable()) // Stateless REST Api: no session cookies are used, so CSRF protection is not required here
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .formLogin(login -> login.disable())
            .httpBasic(httpB -> httpB.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS.toArray(String[]::new))
                .permitAll()
                .requestMatchers("/api/ext/v1/**")
                .authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(loginExceptionHandler)
                .accessDeniedHandler(loginExceptionHandler)
            )
            .addFilterBefore(requestLoggingFilter, SecurityContextHolderFilter.class)
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }

    @Bean
    AuthenticationManager authenticationManager(
        ApiKeyAuthenticationProvider apiKeyAuthenticationProvider
    ) {

        return new ProviderManager(apiKeyAuthenticationProvider);
    }
}
