package cz.ecis.core.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import cz.ecis.core.exception.CampResolveException;
import cz.ecis.core.exception.EcisRuntimeException;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.model.ErrorDto;
import cz.ecis.utils.ExceptionHandlerUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestControllerAdvice
@Order(2)
public class GlobalExceptionHandler {

    public static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )
    )
    public ErrorDto handleAllExceptions(Exception ex) {
        return ExceptionHandlerUtils.handleError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred."
        );
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        BindException.class,
        MethodArgumentTypeMismatchException.class
    })
    @Hidden
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ErrorDto handleValidation(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof BindException be) {
            be.getBindingResult().getFieldErrors()
            .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        }
        else if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            errors.put(mismatch.getName(), "Invalid value: " + mismatch.getValue());
        }

        return ExceptionHandlerUtils.handleError(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "Validation failed",
            errors
        );
    }

    @ExceptionHandler({
        EntityNotExistsException.class,
        NoResourceFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    public ErrorDto handleEntityNotFound(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        switch (ex) {
            case EntityNotExistsException ene ->
                errors.put("id", ene.getId() == null ? null : ene.getId().toString());
            default -> {
                // Not error specification
            }
        }

        return ExceptionHandlerUtils.handleError(HttpStatus.NOT_FOUND, ex, errors);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @Hidden
    public ErrorDto handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ExceptionHandlerUtils.handleError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleHttpMessageConversion(HttpMessageConversionException ex) {

        Map<String, String> errors = new HashMap<>();

        Throwable cause = ExceptionHandlerUtils.rootCause(ex); // <-- důležité

        switch (cause) {
            case UnrecognizedPropertyException unrecognized -> {
                String field = unrecognized.getPropertyName();
                errors.put(field, "Unknown field: '" + field + "'");
            }
            case InvalidFormatException invalidFormat -> {
                String field = ExceptionHandlerUtils.getFieldName(invalidFormat.getPath());

                errors.put(field, String.format(
                    "Invalid value for field '%s'. Expected: %s", field, invalidFormat.getTargetType().getSimpleName()
                ));
            }
            case MismatchedInputException mismatch -> {
                String field = ExceptionHandlerUtils.getFieldName(mismatch.getPath());

                errors.put(field,
                    String.format("Cannot parse field '%s'", field)
                );
            }
            default -> errors.put("json", cause.getMessage());
        }

        return ExceptionHandlerUtils.handleError(
            HttpStatus.BAD_REQUEST,
            "JSON parsing failed",
            errors
        );
    }

    @ExceptionHandler({
        EcisRuntimeException.class
    })
    @Hidden
    public ErrorDto handleEntityIdViolationException(EcisRuntimeException ex) {
        return ExceptionHandlerUtils.handleError(ex.getStatus(), ex, ex.getFieldErrors());
    }

    @ExceptionHandler(CampResolveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Hidden
    public ErrorDto handleCampResolveException(CampResolveException ex) {
        return ExceptionHandlerUtils.handleError(HttpStatus.BAD_REQUEST, ex);
    }

}
