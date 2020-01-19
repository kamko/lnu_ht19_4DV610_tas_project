package se.lnu.research_service_platform.service.adaptation.effectors;

import java.util.List;

import se.lnu.research_service_platform.service.auxiliary.Operation;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.composite.CompositeService;

/**
 * Responsible for changing workflow
 */
public class WorkflowEffector extends AbstractEffector {

    private CacheEffector cacheEffector;

    /**
     * Constructor
     *
     * @param compositeService which composite service to be affected
     */
    public WorkflowEffector(CompositeService compositeService) {
        super(compositeService);
        cacheEffector = new CacheEffector(compositeService);
    }

    /**
     * Update the workflow
     *
     * @param workflow new workflow content
     */
    public void updateWorkflow(String workflow) {
        compositeService.setWorkflow(workflow);
    }

    /**
     * Remove the service from workflow cache
     *
     * @param description the service description
     */
    public void removeService(ServiceDescription description) {
        cacheEffector.removeService(description);
    }

    /**
     * Remove the service from workflow cache with service id
     *
     * @param registerID the service id
     */
    public void removeService(int registerID) {
        cacheEffector.removeService(registerID);
    }

    /**
     * Clear the workflow cache
     */
    public void refreshAllServices() {
        cacheEffector.clearCache();
    }

    /**
     * Clear the workflow cache
     *
     * @param serviceType the service type
     * @param opName      the operation name
     */
    public void refreshAllServices(String serviceType, String opName) {
        cacheEffector.clearCache(serviceType, opName);
    }

    /**
     * Update the service description in the workflow cache
     *
     * @param oldDescription the old service description
     * @param newDescription the new service description
     */
    public void updateServiceDescription(ServiceDescription oldDescription, ServiceDescription newDescription) {
        cacheEffector.updateServiceDescription(oldDescription, newDescription);
    }

    /**
     * Update a custom property of a service in service description. This method only effects the values in cache.
     *
     * @param registerID the service id
     * @param property   the property name
     * @param value      the value
     */
    public void updateServiceCustomProperty(int registerID, String property, Object value) {
        cacheEffector.getService(registerID).getCustomProperties().put(property, value);
    }

    /**
     * If a service failed and composite service is retrying, this method will effect to stop retrying for that service.
     * This method once called will effect only one service invocation/thread.
     */
    public void stopRetrying() {
        compositeService.stopRetrying();
    }

    public void setService(String name, String type, String op) {
        cacheEffector.setOnlyService(name, type, op);
    }

    public void addServices(List<ServiceDescription> services) {
        for (ServiceDescription sd : services) {
            for (Operation op : sd.getOperationList()) {
                cacheEffector.addService(sd, op.getOpName());
            }
        }
    }
}
