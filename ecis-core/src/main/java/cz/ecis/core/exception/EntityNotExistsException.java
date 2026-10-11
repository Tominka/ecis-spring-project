package cz.ecis.core.exception;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;

@Getter
public class EntityNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    private final Map<String, String> map;

    public EntityNotExistsException(Map<String, String> map) {
        this("Record not found", map);
    }

    public EntityNotExistsException(String message) {
        this(message, Map.of());
    }

    public EntityNotExistsException(String message, Serializable id) {
        this(message, Map.of("id", id == null ? null : id.toString()));
    }

    public EntityNotExistsException(String message, Map<String, String> map) {
        super(message);
        this.map = map;
    }
}
