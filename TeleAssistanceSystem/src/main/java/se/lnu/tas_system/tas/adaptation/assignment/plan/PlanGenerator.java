package se.lnu.tas_system.tas.adaptation.assignment.plan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;

public class PlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(PlanGenerator.class);

    public static Plan retryWithService(ServiceDescription sd, String opName) {
        log.info("Generating plan for service retry. New service={}, op={}", sd.getServiceName(), opName);

        return new Plan(
                AdaptationOption.RETRY_WITH_EQUIVALENT_SERVICE,
                sd.getServiceName(), sd.getServiceType(), opName
        );
    }

    public static Plan planReload(String serviceType, String opName) {
        return new Plan(
                AdaptationOption.RELOAD_SERVICE_REGISTRY,
                serviceType, opName
        );
    }

    public static Plan noAction() {
        return new Plan(AdaptationOption.NO_ACTION);
    }

}
