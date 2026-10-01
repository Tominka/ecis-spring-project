package cz.ecis.core.exception;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class EntityNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    private final Serializable id;

    public EntityNotExistsException(String message, Serializable id) {
        super(message);
        this.id = id;
    }
}
