package cz.ecis.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import cz.ecis.core.model.ErrorDto;
import tools.jackson.core.JacksonException.Reference;

public class ExceptionHandlerUtils {

    public static final Logger LOGGER = LogManager.getLogger(ExceptionHandlerUtils.class);

    private ExceptionHandlerUtils() {
        // utility class
    }

    public static Throwable rootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static String getFieldName(List<Reference> pathList) {
        return pathList.isEmpty() ? "unknown" : pathList.get(0).getPropertyName();
    }

    public static ErrorDto handleError(HttpStatus status, Throwable e) {
        LOGGER.error("Exception handled", e);
        return handleError(status, e.getMessage(), null);
    }

    public static ErrorDto handleError(HttpStatus status, String message) {
        return handleError(status, message, null);
    }

    public static ErrorDto handleError(HttpStatus status, Throwable e, Map<String, String> errors) {
        LOGGER.error("Exception handled", e);
        return handleError(status, e.getMessage(), errors);
    }

    public static ErrorDto handleError(HttpStatus status, String message, Map<String, String> errors) {
        return ErrorDto.builder()
            .status(status.value())
            .message(message)
            .errors(errors == null ? new HashMap<>() : errors)
            .timestamp(OffsetDateTime.now(ZoneId.systemDefault()).toString())
        .build();
    }
}
