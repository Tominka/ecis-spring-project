package cz.ecis.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class EntityVersionViolation extends EcisRuntimeException {

	private static final long serialVersionUID = 1L;

    private final Object version1;
    private final Object version2;

    public EntityVersionViolation(String message, Object version1, Object version2) {
        super(message, HttpStatus.CONFLICT);
        this.version1 = version1;
        this.version2 = version2;
        this.putFieldSafe("version1", this.version1);
        this.putFieldSafe("version2", this.version2);
    }
}
