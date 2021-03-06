package se.lnu.research_service_platform.service.adaptation.effectors;

import java.util.Collections;
import java.util.List;

import se.lnu.research_service_platform.service.auxiliary.Operation;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.composite.CompositeService;

/**
 * Responsible for changing cache
 */
public class CacheEffector extends AbstractEffector {

    private CompositeService compositeService;

    /**
     * Constructor
     *
     * @param compositeService which composite service to be affected
     */
    public CacheEffector(CompositeService compositeService) {
        super(compositeService);
        this.compositeService = compositeService;
    }

    /**
     * Remove service from cache
     *
     * @param description the service description
     */
    public void removeService(ServiceDescription description) {
        compositeService.getCache().remove(description);
    }

    /**
     * Remove service from cache with register id
     *
     * @param registerID the unique register id
     */
    public void removeService(int registerID) {
        compositeService.getCache().remove(registerID);
    }

    /**
     * Remove service from cache with service description and operation name
     *
     * @param description the service description
     * @param opName      the operation name
     */
    public void removeService(ServiceDescription description, String opName) {
        compositeService.getCache().remove(description, opName);
    }

    /**
     * Refresh the cache
     * all services will be removed.
     */
    public void clearCache() {
        compositeService.clearCache();
    }

    /**
     * Remove and Reload all services with same type and operation.
     * This method was previously named as getAllServices method in version 1.6.
     *
     * @param serviceType the service type
     * @param opName      the operation name
     */
    public void clearCache(String serviceType, String opName) {
        compositeService.getCache().remove(serviceType, opName);
        compositeService.reloadServicesCache(serviceType, opName);
    }

    /**
     * Remove and Reload allco  services with same service description and operation
     *
     * @param description the service description
     * @param opName      the operation name
     * @return a list of service descriptions after refreshing
     */
    public List<ServiceDescription> clearCache(ServiceDescription description, String opName) {
        removeService(description, opName);
        return compositeService.reloadServicesCache(description.getServiceType(), opName);
    }

    /**
     * Update service description
     *
     * @param oldDescription the old service description
     * @param newDescription the new service description
     */
    public void updateServiceDescription(ServiceDescription oldDescription, ServiceDescription newDescription) {
        if (oldDescription.getRegisterID() == newDescription.getRegisterID()) {
            for (Operation operation : oldDescription.getOperationList())
                compositeService.getCache().update(oldDescription, newDescription, operation.getOpName());
        }
    }

    /**
     * Returns a service description by its registration id
     *
     * @param registerID registerId the service register id
     * @return the service description
     */
    public ServiceDescription getService(int registerID) {
        return compositeService.getCache().getServiceDescription(registerID);
    }

    public ServiceDescription setOnlyService(String serviceName, String type, String op) {
        return compositeService.loadOnlyServiceIntoCache(serviceName, type, op).get(0);
    }

    public void addService(ServiceDescription sd, String op) {
        compositeService.getCache().add(sd.getServiceType(), op, Collections.singletonList(sd));
    }
}
