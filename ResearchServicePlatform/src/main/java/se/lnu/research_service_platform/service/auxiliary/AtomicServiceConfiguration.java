package se.lnu.research_service_platform.service.auxiliary;

/**
 * 
 * The configuration of atomic service with properties
 */
public @interface AtomicServiceConfiguration {
    public boolean MultipeThreads() default false;
    public int MaxNoOfThreads() default -1;
    public int MaxQueueSize() default 0;
}
