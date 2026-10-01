package cz.ecis.core.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EcisRoleEnum {
    USER("00000"),
    USER2("22222"),
    CAMP_CREATE("10002"),
    CAMP_EDIT("10003"),
    
    CHILDREN_READ("20000"),
    
    ;

    private final String code;

}
