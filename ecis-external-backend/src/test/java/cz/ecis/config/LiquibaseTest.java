package cz.ecis.config;

import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Kombinovaná anotace pro Liquibase migrace před a po testu
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TestExecutionListeners(mergeMode =  TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS, listeners = LiquibaseExtension.class)
public @interface LiquibaseTest {

}
