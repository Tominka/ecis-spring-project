package cz.ecis.core.security.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cz.ecis.core.EcisContext;
import cz.ecis.core.handler.LoginExceptionHandler;
import cz.ecis.core.security.EcisUserDetails;
import cz.ecis.core.security.JwtAuthenticationToken;
import cz.ecis.core.security.jwt.JwtService;
import cz.ecis.core.security.jwt.JwtService.JwtType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final Logger LOGGER = LogManager.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginExceptionHandler loginExceptionHandler;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());
        String username;

        Claims jwtClaims = null;

        try {
            jwtClaims = this.jwtService.extractClaims(token, JwtType.ACCESS);
            username = jwtService.extractUsername(jwtClaims);
            EcisContext.getRequest().getEntry().setSessionId(jwtService.extractSessionId(jwtClaims));
        } catch (Exception ex) {
            LOGGER.error("Error parsing JWT token: {}", ex.getMessage());
            loginExceptionHandler.commence(request, response,
                new AuthenticationServiceException("Invalid JWT token", ex));
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
                JwtAuthenticationToken authRequest = new JwtAuthenticationToken(username, token);
                authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                Authentication authResult = authenticationManager.authenticate(authRequest);

                EcisUserDetails details = (EcisUserDetails) authResult.getPrincipal();

                if (details != null && details.getEnt() != null && !details.getEnt().getPasswordChanged().withNano(0)
                        .equals(jwtService.extractPasswordChanged(jwtClaims))) {
                    loginExceptionHandler.commence(request, response, new CredentialsExpiredException("Relace je neplatná"));
                    return;
                }

                EcisContext.getRequest().getEntry().setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } catch (AuthenticationException ex) {
                LOGGER.error("JWT authentication failed: {}", ex.getMessage());
                loginExceptionHandler.commence(request, response, ex);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}