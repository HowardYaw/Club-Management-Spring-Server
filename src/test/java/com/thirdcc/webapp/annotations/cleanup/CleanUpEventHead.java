package com.thirdcc.webapp.annotations.cleanup;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(value = {
    CleanUpEventHeadTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class
})
public @interface CleanUpEventHead {

}
