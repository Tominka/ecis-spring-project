package cz.ecis.config.liquibase;

import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TestExecutionListeners(mergeMode =  TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS, listeners = LiquibaseExtension.class)
public @interface LiquibaseTest {

    String uuid() default "";
}