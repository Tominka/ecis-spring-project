package cz.ecis.core.security.annotation;

import java.lang.annotation.*;

import cz.ecis.core.security.EcisRoleEnum;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CampSecured {

    EcisRoleEnum[] roles() default {};
}