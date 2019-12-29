package se.lnu.tas_system.tas.services.qos;

import java.util.List;
import java.util.Random;

import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.workflow.AbstractQoSRequirement;

public class PreferencesQoS implements AbstractQoSRequirement {

    Random rand = new Random();

    @Override
    public ServiceDescription applyQoSRequirement(List<ServiceDescription> serviceDescriptions, String opName, Object[] params) {

        // Search Preferred Service
        for (ServiceDescription serviceDescription : serviceDescriptions) {
            if (serviceDescription.getCustomProperties().containsKey("preferred")) {
                boolean value = (boolean) serviceDescription.getCustomProperties().get("preferred");
                if (value)
                    return serviceDescription;
            }
        }

        // If there is no preferred service found then select a service randomly
        int index = rand.nextInt(serviceDescriptions.size());
        return serviceDescriptions.get(index);

    }
}
