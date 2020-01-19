package se.lnu.tas_system.tas.adaptation.assignment.t1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.Knowledge;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;
import se.lnu.tas_system.tas.adaptation.assignment.plan.PlanGenerator;

public class T1Analyzer {

    private static final Logger log = LoggerFactory.getLogger(T1Analyzer.class);

    private final Knowledge knowledge;
    private final ServiceRegistry registry;

    public T1Analyzer(Knowledge knowledge, ServiceRegistry registry) {
        this.knowledge = knowledge;
        this.registry = registry;
    }

    Plan analyzeAfterFailure(ServiceDescription sd, String op) {
        log.info("Analyzing failure of service={} op={}", sd, op);
        List<ServiceDescription> possibleServices = registry.lookup(sd.getServiceType(), op)
                .stream()
                .filter(fsd -> !fsd.getServiceName().equals(sd.getServiceName()))
                .collect(Collectors.toList());


        ServiceDescription best = findCheapest(possibleServices, op);

        return PlanGenerator.retryWithService(best, op);
    }

    List<Plan> analyzeConfigurationChange() {
        if (areGoalsViolated()) {
            return Arrays.asList(
                    PlanGenerator.noAction(),
                    PlanGenerator.noAction()
            );
        }

        return Collections.singletonList(PlanGenerator.noAction());
    }

    private boolean areGoalsViolated() {
        return false;
    }

    private ServiceDescription findCheapest(List<ServiceDescription> services, String op) {
        double minValue = Double.MAX_VALUE;
        ServiceDescription minService = null;

        for (ServiceDescription sd : services) {
            if (sd.getOperationCost(op) < minValue) {
                minService = sd;
                minValue = sd.getOperationCost(op);
            }
        }

        return minService;
    }
}
