package cz.ecis.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class EntityIdViolation extends EcisRuntimeException {

	private static final long serialVersionUID = 1L;

	private final Object id1;
    private final Object id2;

    public EntityIdViolation(String message, Object id1, Object id2) {
        super(message, HttpStatus.BAD_REQUEST);
        this.id1 = id1;
        this.id2 = id2;
        this.putFieldSafe("id1", id1);
        this.putFieldSafe("id2", id2);
    }

    public EntityIdViolation(Object id1, Object id2) {
        this("Entity Id violation", id1, id2);
    }
    
}
