package se.lnu.research_service_platform.service.composite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.probes.CostProbe;
import se.lnu.research_service_platform.service.adaptation.probes.WorkflowProbe;
import se.lnu.research_service_platform.service.auxiliary.AbstractService;
import se.lnu.research_service_platform.service.auxiliary.CompositeServiceConfiguration;
import se.lnu.research_service_platform.service.auxiliary.Configuration;
import se.lnu.research_service_platform.service.auxiliary.LocalOperation;
import se.lnu.research_service_platform.service.auxiliary.Param;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;
import se.lnu.research_service_platform.service.auxiliary.TimeOutError;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.research_service_platform.service.workflow.AbstractQoSRequirement;
import se.lnu.research_service_platform.service.workflow.WorkflowEngine;

/**
 * Providing an abstraction to create composite services
 */
public abstract class CompositeService extends AbstractService {

    private static final Logger log = LoggerFactory.getLogger(CompositeService.class);

    private String workflow;
    // Initializing probes
    private CostProbe costProbe = new CostProbe();
    private WorkflowProbe workflowProbe = new WorkflowProbe();
    // Initializing effectors
    //private ConfigurationEffector configurationEffector = new ConfigurationEffector(this);

    // This variable will effect only one thread/invocation of the workflow
    private AtomicBoolean stopRetrying = new AtomicBoolean(false);

    /**
     * Set the workflow
     *
     * @param workflow the new workflow
     */
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    private Map<String, AbstractQoSRequirement> qosRequirements = new HashMap<String, AbstractQoSRequirement>();

    private SDCache cache;
    private CacheHistory cacheHistory;

    /**
     * Return the cache
     *
     * @return the current cache
     */
    public SDCache getCache() {
        return cache;
    }

    public void clearCache() {
        cache.refresh();
        cacheHistory.clear();
    }

    @Override
    protected void readConfiguration() {
        try {
            CompositeServiceConfiguration annotation = this.getClass().getAnnotation(
                    CompositeServiceConfiguration.class);
            if (annotation != null) {
                this.configuration = new Configuration(
                        annotation.MultipeThreads(),
                        annotation.MaxNoOfThreads(),
                        annotation.MaxQueueSize(),
                        annotation.Timeout(),
                        annotation.IgnoreTimeOutError(),
                        annotation.MaxRetryAttempts(),
                        annotation.SDCacheMode(),
                        annotation.SDCacheShared(),
                        annotation.SDCacheTimeout(),
                        annotation.SDCacheSize());
            } else {
                // the default configuration
                this.configuration = new Configuration(false, 1, 0, 0, false,
                        1, true, true, -1, -1);
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    /**
     * Constructor
     *
     * @param serviceName     the service name
     * @param serviceEndpoint the service endpoint
     * @param workflow        the workflow
     */
    public CompositeService(String serviceName, String serviceEndpoint, String workflow) {
        super(serviceName, serviceEndpoint);
        this.workflow = workflow;

        if (!configuration.SDCacheMode) {
            log.error("Warning! Cache mode cannot be turned off.");
        } else if (!configuration.SDCacheShared) {
            log.error("Warning! Cache mode sharing cannot be turned off.");
        } else if (configuration.SDCacheSize == 0) {
            log.error("Warning! Cache size cannot be equal to zero.");
        }

        //if (this.configuration.SDCacheMode) {
        cache = new SDCache();
        cacheHistory = new CacheHistory();
        //}
    }

    /**
     * Add QoS requirement
     *
     * @param requirementName the QoS requirement name
     * @param qosRequirement  the QoS requirement Object
     */
    public void addQosRequirement(String requirementName, AbstractQoSRequirement qosRequirement) {
        qosRequirements.put(requirementName, qosRequirement);
    }

    /**
     * Return QoS requirements
     *
     * @return the current QoS requirements
     */
    public Map<String, AbstractQoSRequirement> getQosRequirements() {
        return qosRequirements;
    }

    /**
     * Returns list of QoS names added in to the composite service
     *
     * @return list of QoS requirement names
     */
    @ServiceOperation
    public List<String> getQosRequirementNames() {
        return new ArrayList<>(qosRequirements.keySet());
    }

    /**
     * Invoke this composite service to start a workflow with specific QoS requirements
     * and initial parameters for the workflow
     *
     * @param qosRequirement the QoS requirement name for executing the workflow
     * @param params         the initial parameters for the workflow
     * @return the result after executing the workflow
     */
    @ServiceOperation
    public Object invokeCompositeService(String qosRequirement, Object[] params) {
        //AbstractQoSRequirement qosRequirement = qosRequirements.get(qosRequirementName);

        // If SDCache shared is not on then a new cache object for the workflow should be created
        //ToDo: Cache is shared among all the workflow invocations. separate local cache is not supported yet.
        //SDCache sdCache = configuration.SDCacheShared == true ? cache : new SDCache() ;
        //WorkflowEngine engine = new WorkflowEngine(this, sdCache);
        WorkflowEngine engine = new WorkflowEngine(this);
        workflowProbe.notifyWorkflowStarted(qosRequirement, params);
        Object result = engine.executeWorkflow(workflow, qosRequirement, params);
        workflowProbe.notifyWorkflowEnded(result, qosRequirement, params);
        return result;
    }

    @Override
    public Object invokeOperation(String opName, Param[] params) {
        // System.out.println(opName);
        for (Method operation : this.getClass().getMethods()) {
            if (operation.getAnnotation(ServiceOperation.class) != null) {
                try {
                    if (operation.getName().equals(opName)) {
                        Class<?>[] paramTypes = operation.getParameterTypes();
                        int size = paramTypes.length;
                        if (size == params.length) {
                            Object[] args = new Object[size];
                            for (int i = 0; i < size; i++) {
                                args[i] = params[i].getValue();
                            }
                            return operation.invoke(this, args);
                        }
                    }
                } catch (Exception e) {
                    log.error("The operation name or params are not valid. Please check and send again!", e);
                }
            }
        }
        return null;
    }


    /**
     * Search through service registry to get the list of service descriptions
     *
     * @param serviceType the service type
     * @param opName      the operation name
     * @return list of service descriptions with the same service type and operation name
     */
    public List<ServiceDescription> lookupService(String serviceType, String opName) {
        // try from cache
        List<ServiceDescription> services = cache.get(serviceType, opName);
        if (services != null && !services.isEmpty()) {
            return services;
        }

        // if nothing in cache
        // and it's first time requesting this service - load all into cache
        if (!cacheHistory.wasAlreadyRequested(serviceType, opName)) {
            reloadServicesCache(serviceType, opName);
        }

        // load service from cache
        return cache.get(serviceType, opName);
    }

    @SuppressWarnings("unchecked")
    public List<ServiceDescription> reloadServicesCache(String serviceType, String opName) {
        List<ServiceDescription> serviceDescriptions = (List<ServiceDescription>) this.sendRequest(
                ServiceRegistry.NAME, ServiceRegistry.ADDRESS, true,
                "lookup", serviceType, opName);
        cache.add(serviceType, opName, serviceDescriptions);

        return serviceDescriptions;
    }

    public List<ServiceDescription> loadOnlyServiceIntoCache(String name, String type, String op) {
        List<ServiceDescription> services = reloadServicesCache(type, op);
        clearCache();

        List<ServiceDescription> singleServiceList = services.stream()
                .filter(sd -> sd.getServiceName().equals(name))
                .collect(Collectors.toList());

        cache.add(type, op, singleServiceList);

        return singleServiceList;
    }

    /**
     * Returns the cost probe
     *
     * @return the cost probe for this composite service
     */
    public CostProbe getCostProbe() {
        return costProbe;
    }

    /**
     * Return the workflow probe
     *
     * @return the workflow probe for this composite service
     */
    public WorkflowProbe getWorkflowProbe() {
        return workflowProbe;
    }

    /**
     * Return the configuration effector
     * @return the configuration effector for this composite service
     */
    // public ConfigurationEffector getConfigurationEffector() {
    //	return configurationEffector;
    //}

    /**
     * Returns true if composite service cache contains instances of the specific service type with operation name
     *
     * @param serviceType the service type
     * @param opName      the operation name
     * @return true if cache has service with the same type and operation, otherwise false
     */
    public boolean containServices(String serviceType, String opName) {
        return cache.containsCache(serviceType, opName);
    }


    /**
     * Get service description using register ID of the service from cache
     *
     * @param registerId the register id
     * @return the service description
     */
    public ServiceDescription getServiceDescription(int registerId) {
        return cache.getServiceDescription(registerId);
    }

    protected ServiceDescription applyQoSRequirement(String qosRequirementName, List<ServiceDescription> descriptions, String opName, Object... params) {
        AbstractQoSRequirement qosRequirement = qosRequirements.get(qosRequirementName);
        if (qosRequirement == null) {
            log.warn("QoS requirement is null. To select among multiple services, a QoS requirement must have been provided.");
            log.warn("Selecting a service randomly...");
            return descriptions.get(new Random().nextInt(descriptions.size()));
        }
        return qosRequirement.applyQoSRequirement(descriptions, opName, params);
    }

    /**
     * Invoke a service operation
     *
     * @param qosRequirement the applied QoS requirements
     * @param serviceName    the service name
     * @param opName         the operation name
     * @param params         the parameters of the operation
     * @return the result
     */
    public Object invokeServiceOperation(String qosRequirement, String serviceName, String opName, Object[] params) {

        int timeout = this.getConfiguration().timeout;
        Object resultVal = null;
        int retryAttempts = 0;
        stopRetrying.set(false);
        do {
            List<ServiceDescription> services = lookupService(serviceName, opName);
            if (services == null || services.size() == 0) {
                log.error("ServiceName: {}.{} not found!", serviceName, opName);
                getWorkflowProbe().notifyServiceNotFound(serviceName, opName);
                continue;
            }

            // Apply strategy
            ServiceDescription service = applyQoSRequirement(qosRequirement, services, opName, params);

            //System.out.println("Operation " + service.getServiceType() + "." + opName + " has been selected with following custom properties:"
            //    + service.getCustomProperties());

            this.getWorkflowProbe().notifyServiceOperationInvoked(service, opName, params);

            int maxResponseTime = timeout != 0 ? timeout : service.getResponseTime() * 3;
            resultVal = this.sendRequest(service.getServiceType(), service.getServiceEndpoint(), true, maxResponseTime, opName, params);

            if (resultVal instanceof TimeOutError) {
                this.getWorkflowProbe().notifyServiceOperationTimeout(service, opName, params);
            } else {
                this.getWorkflowProbe().notifyServiceOperationReturned(service, resultVal, opName, params);
                this.getCostProbe().notifyCostSubscribers(service, opName);
            }

            if (stopRetrying.get()) {
                stopRetrying.set(false);
                break;
            }

            retryAttempts++;
        } while (resultVal instanceof TimeOutError && retryAttempts < this.getConfiguration().maxRetryAttempts);

        return resultVal;
    }

    /**
     * Invoke the operation without the annotation "ServiceOperation"
     *
     * @param opName the operation name
     * @param params the parameters of the operation
     * @return the result
     */
    public Object invokeLocalOperation(String opName, Object[] params) {
        for (Method operation : this.getClass().getMethods()) {
            if (operation.getAnnotation(LocalOperation.class) != null) {
                if (operation.getName().equals(opName)) {
                    try {
                        return operation.invoke(this, params);
                    } catch (Exception e) {
                        log.error("Error", e);
                    }
                }
            }
        }
        throw new RuntimeException("Local operation " + opName + " is not found.");
    }

    /**
     * If a service failed and composite service is retrying, this method will effect to stop retrying for that service.
     * This method once called will effect only one service invocation/thread.
     */
    public void stopRetrying() {
        stopRetrying.set(true);
    }

    private static class CacheHistory {
        private final Set<String> seen = new HashSet<>();

        void clear() {
            seen.clear();
        }

        boolean wasAlreadyRequested(String serviceType, String opName) {
            return !seen.add(name(serviceType, opName));
        }

        private String name(String serviceType, String opName) {
            return serviceType + "." + opName;
        }
    }
}
