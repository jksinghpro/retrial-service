package jk.util.retrial.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to indication a method is retriable
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RetryWith {

    String alias() default "none";
}
