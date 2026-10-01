package cz.ecis.core.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.ecis.core.model.ErrorDto;
import cz.ecis.utils.ExceptionHandlerUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    public static final Logger LOGGER = LogManager.getLogger();

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        this.handle(response, authException);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.handle(response, accessDeniedException);
    }

    private void handle(HttpServletResponse response, Exception ex) throws IOException {
        ErrorDto responseDto;

        switch (ex) {
            case BadCredentialsException _ ->
                responseDto = ExceptionHandlerUtils.handleError(HttpStatus.UNAUTHORIZED, "Bad credentials");
            case InsufficientAuthenticationException _ ->
                responseDto = ExceptionHandlerUtils.handleError(HttpStatus.UNAUTHORIZED, "Unauthorized");
            case AuthenticationServiceException _ ->
                responseDto = ExceptionHandlerUtils.handleError(HttpStatus.UNAUTHORIZED, ex.getMessage());
            case DisabledException _ ->
                responseDto = ExceptionHandlerUtils.handleError(HttpStatus.NOT_FOUND, "User not found or is disabled");
            case CredentialsExpiredException e ->
                responseDto = ExceptionHandlerUtils.handleError(HttpStatus.UNAUTHORIZED, e.getMessage());
            default ->
                responseDto = ExceptionHandlerUtils.handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error occured");
        }

        write(responseDto, response);

        LOGGER.error(ex);

    }

    private void write(ErrorDto dto, HttpServletResponse response) throws IOException {
        response.setStatus(dto.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), dto);
    }
}

