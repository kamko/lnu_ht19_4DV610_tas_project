package se.lnu.research_service_platform.service.auxiliary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/**
 *
 * Annotation for local operations, which cannot be invoked remotely
 *
 */
public @interface LocalOperation {
}
