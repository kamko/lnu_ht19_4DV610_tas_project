package se.lnu.tas_system.tas.services.qos;

import java.util.HashMap;
import java.util.List;

import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.workflow.AbstractQoSRequirement;

public class ReliabilityQoS implements AbstractQoSRequirement {

    @Override
    public ServiceDescription applyQoSRequirement(List<ServiceDescription> serviceDescriptions, String opName, Object[] params) {
        Double minFailureRate = Double.MAX_VALUE;
        int index = 0;
        Double cost;
        HashMap properties;
        for (int i = 0; i < serviceDescriptions.size(); i++) {
            properties = serviceDescriptions.get(i).getCustomProperties();
            if (properties.containsKey("FailureRate")) {
                cost = (Double) properties.get("FailureRate");
                if (cost < minFailureRate) {
                    minFailureRate = cost;
                    index = i;
                }
            }
        }

        return serviceDescriptions.get(index);
    }

}
