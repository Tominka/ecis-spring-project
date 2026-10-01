package cz.ecis.core.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class EcisRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected final Map<String, String> fieldErrors;
    private final HttpStatus status;

    public EcisRuntimeException(String message, HttpStatus status) {
        this(message, new HashMap<>(), status);
    }

    public EcisRuntimeException(String message, Map<String, String> fieldErrors, HttpStatus status) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.status = status;
    }

    protected void putFieldSafe(String key, Object obj) {
        if (key != null) {
            this.fieldErrors.put(key, obj == null ? "null" : obj.toString());
        }
    }
    

}
