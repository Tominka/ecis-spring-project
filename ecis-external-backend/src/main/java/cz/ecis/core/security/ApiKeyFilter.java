package cz.ecis.core.security;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final Logger LOGGER = LogManager.getLogger(ApiKeyFilter.class);

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-Key");

        // If no API key header is present, skip API-key authentication so
        // public paths (permitAll) can be handled by the security chain.
        if (StringUtils.isBlank(apiKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        ApiKeyAuthenticationToken authRequest = new ApiKeyAuthenticationToken(apiKey);

        authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        try {
            Authentication authResult = authenticationManager.authenticate(authRequest);

            SecurityContextHolder.getContext().setAuthentication(authResult);

            filterChain.doFilter(request, response);

        } catch (AuthenticationException _) {

            SecurityContextHolder.clearContext();

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
        }

    }

}