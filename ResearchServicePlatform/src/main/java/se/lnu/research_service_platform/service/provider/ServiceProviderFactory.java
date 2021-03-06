package se.lnu.research_service_platform.service.provider;

import se.lnu.research_service_platform.service.messagingservice.MessagingServiceProvider;

/**
 * This class helps to choose a service provider.
 *
 * @author M. Usman Iftikhar, Yifan Ruan
 */
public class ServiceProviderFactory {

    /**
     * This method helps to choose a service provider from list of available service providers.
     *
     * @return ServiceProvider  the specific service provider
     */
    public static ServiceProvider createServiceProvider() {
        return new MessagingServiceProvider();
    }
}
