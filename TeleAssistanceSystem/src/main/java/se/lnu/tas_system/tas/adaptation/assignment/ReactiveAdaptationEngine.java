package se.lnu.tas_system.tas.adaptation.assignment;

import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.tas_system.tas.adaptation.AdaptationEngine;

public interface ReactiveAdaptationEngine extends AdaptationEngine {

    void handleServiceFailure(ServiceDescription service, String opName, Object[] params);

    void handleServiceNotFound(String serviceType, String opName);

    default void handleWorkflowEnd(Object result, String qosRequirement, Object[] params) {
    }
}
