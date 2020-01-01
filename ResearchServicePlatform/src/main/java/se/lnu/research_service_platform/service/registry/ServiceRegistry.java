package se.lnu.research_service_platform.service.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;
import se.lnu.research_service_platform.service.auxiliary.ServiceRegistryInterface;

/**
 * Service for registering and finding services
 *
 * @author Yifan Ruan (ry222ad@student.lnu.se)
 */
public class ServiceRegistry extends AtomicService implements ServiceRegistryInterface {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

    private HashMap<Integer, ServiceDescription> serviceList = new HashMap<>();
    private Map<String, Set<ServiceDescription>> services = new HashMap<String, Set<ServiceDescription>>();
    private int serviceCount = 0;

    /**
     * Constructor
     */
    public ServiceRegistry() {
        super(NAME, ADDRESS);
    }

    /**
     * Return list of service names
     *
     * @return list of service names
     */
    public List<String> getAllServices() {
        List<String> allServices = new ArrayList<>();
        for (ServiceDescription service : serviceList.values()) {
            allServices.add(service.getServiceName());
        }
        return allServices;
    }

    @ServiceOperation
    public int register(ServiceDescription description) {
        serviceCount++;
        description.setRegisterID(serviceCount);
        serviceList.put(serviceCount, description);
        String type = description.getServiceType();
        if (services.containsKey(type)) {
            services.get(type).add(description);
        } else {
            Set<ServiceDescription> set = new HashSet<>();
            set.add(description);
            services.put(type, set);
        }
        log.info("Service {} is registered.", description.getServiceType());
        return serviceCount;
    }

    @ServiceOperation
    public void unRegister(int registerID) {
        ServiceDescription service = serviceList.get(registerID);
        serviceList.remove(registerID);
        Set<ServiceDescription> set = services.get(service.getServiceType());
        set.remove(service);
        if (set.size() == 0)
            services.remove(service.getServiceType());
        log.info("Service {} is registered.", service.getServiceType());
    }

    @ServiceOperation
    public List<ServiceDescription> lookup(String type, String opName) {
        log.debug("Lookup {}.{}", type, opName);
        List<ServiceDescription> list = new ArrayList<>();
        if (services.containsKey(type)) {
            Set<ServiceDescription> set = services.get(type);
            for (ServiceDescription service : set) {
                if (service.containsOperation(opName))
                    list.add(service);
            }
        }
        return list;
    }

    public ServiceDescription getService(String serviceName) {
        for (ServiceDescription description : serviceList.values()) {
            if (description.getServiceName().equals(serviceName))
                return description;
        }
        return null;
    }

    public ServiceDescription getService(int registerId) {
        return serviceList.get(registerId);
    }

    public void removeService(ServiceDescription description) {
        String type = description.getServiceType();
        int registerID = description.getRegisterID();

        serviceList.remove(registerID);

        if (services.containsKey(type))
            services.get(type).remove(description);
    }

    public void addService(ServiceDescription description) {
        String type = description.getServiceType();
        int registerID = description.getRegisterID();
        serviceList.put(registerID, description);
        services.get(type).add(description);
    }

    /**
     * Update service description
     *
     * @param description the new service description
     */
    @ServiceOperation
    public void update(ServiceDescription description) {
        ServiceDescription oldDescription = serviceList.remove(description.getRegisterID());
        Set<ServiceDescription> descriptions = services.get(description.getServiceType());
        descriptions.remove(oldDescription);
        descriptions.add(description);
        log.debug("Update service description");
    }

    public static void main(String[] args) {
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        serviceRegistry.startService();
    }
}
