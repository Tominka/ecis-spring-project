package cz.ecis.core.exception;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class EntityDuplicationException extends EcisRuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final UUID id;

    public EntityDuplicationException(UUID id, String message, Map<String, String> fieldErrors) {
        super(message, fieldErrors, HttpStatus.CONFLICT);
        this.id = id;
    }
}
