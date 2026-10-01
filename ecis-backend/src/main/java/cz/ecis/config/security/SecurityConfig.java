package cz.ecis.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import cz.ecis.core.RequestLoggingFilter;
import cz.ecis.core.handler.LoginExceptionHandler;
import cz.ecis.core.security.EcisUserDetailsService;
import cz.ecis.core.security.filter.JwtAuthFilter;
import cz.ecis.core.security.jwt.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final EcisUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final LoginExceptionHandler loginExceptionHandler;
    private final RequestLoggingFilter requestLoggingFilter;

    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/v1/sec/login",
        // "/api/v1/sec/refresh",
        "/api/v1/sec/request-password-reset",
        "/api/v1/sec/password-reset",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**"
    );

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable) // Stateless JWT auth: no session cookies are used, so CSRF protection is not required here
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS.toArray(String[]::new))
                .permitAll()
                .requestMatchers("/api/v1/**")
                .authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(loginExceptionHandler)
                .accessDeniedHandler(loginExceptionHandler)
            )
            .authenticationProvider(jwtAuthenticationProvider())
            .addFilterBefore(requestLoggingFilter, SecurityContextHolderFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // .addFilterAfter(campContextFilter,
            //     UsernamePasswordAuthenticationFilter.class)
            // .userDetailsService(userDetailsService)
        .build();
    }

    @Bean
    AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(userDetailsService);
    }

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
