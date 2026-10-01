package cz.ecis.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class IllegalRecordStateException extends EcisRuntimeException {

	private static final long serialVersionUID = 1L;

    private final String reason;

    public IllegalRecordStateException(String message, String reason) {
        super(message, HttpStatus.BAD_REQUEST);
        this.reason = reason;
        if (StringUtils.isNotBlank(reason)) {
            this.putFieldSafe("reason", reason);
        }
    }

    public IllegalRecordStateException(String message) {
        this(message, null);
    }

}
