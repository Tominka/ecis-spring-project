package cz.ecis.core.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.model.ErrorDto;
import cz.ecis.utils.ExceptionHandlerUtils;
import io.swagger.v3.oas.annotations.Hidden;

@RestControllerAdvice
@Order(1)
public class SecurityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Hidden
    public ErrorDto handleBadCredentials(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        switch (ex) {
            case EntityNotExistsException ene ->
                errors.put("id", ene.getId() == null ? null : ene.getId().toString());
            default -> {
                // Not error specification
            }
        }

        return ExceptionHandlerUtils.handleError(HttpStatus.FORBIDDEN, ex, errors);
    }

    // @ExceptionHandler({
    //     UsernameNotFoundException.class,
    //     DisabledException.class
    // })
    // @ResponseStatus(HttpStatus.NOT_FOUND)
    // @Hidden
    // public ErrorDto handleUserNotFound(Exception ex) {
    //     return ExceptionHandlerUtils.handleError(HttpStatus.NOT_FOUND, "User is not exist or is disabled", null);
    // }

    @Hidden
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto hadnleAccessDenied(AccessDeniedException ex) {
        return ExceptionHandlerUtils.handleError(HttpStatus.FORBIDDEN, ex);
    }

    // @Hidden
    // @ExceptionHandler(CredentialsExpiredException.class)
    // @ResponseStatus(HttpStatus.UNAUTHORIZED)
    // public ErrorDto handleCredentialsExpired(CredentialsExpiredException ex) {
    //     return ExceptionHandlerUtils.handleError(HttpStatus.UNAUTHORIZED, ex);
    // }

}
