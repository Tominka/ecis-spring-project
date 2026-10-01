package cz.ecis.core.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorDto {

    private int status;
    private String message;
    private Map<String, String> errors;
    private String timestamp;
}
