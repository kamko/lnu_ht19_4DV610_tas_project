package se.lnu.research_service_platform.service.auxiliary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The configuration of atomic service with properties
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AtomicServiceConfiguration {
    public boolean MultipeThreads() default false;

    public int MaxNoOfThreads() default -1;

    public int MaxQueueSize() default 0;
}
