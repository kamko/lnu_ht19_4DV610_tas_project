package se.lnu.tas_system.tas.adaptation.assignment.t1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.tas_system.tas.adaptation.assignment.analyze.Analyzer;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.Knowledge;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.ThresholdGoal;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;
import se.lnu.tas_system.tas.adaptation.assignment.plan.PlanGenerator;

public class T1Analyzer implements Analyzer {

    private static final Logger log = LoggerFactory.getLogger(T1Analyzer.class);

    private final ServiceRegistry registry;

    private final ThresholdGoal thresholdGoal;

    public T1Analyzer(Knowledge knowledge, ServiceRegistry registry) {
        this.registry = registry;

        this.thresholdGoal = new ThresholdGoal(
                ThresholdGoal.Type.UNDER,
                0.1,
                knowledge::getCompositeFailureRate
        );
    }

    @Override
    public Plan analyzeAfterFailure(ServiceDescription sd, String op) {
        log.info("Analyzing failure of service={} op={}", sd, op);
        List<ServiceDescription> possibleServices = registry.lookup(sd.getServiceType(), op)
                .stream()
                .filter(fsd -> !fsd.getServiceName().equals(sd.getServiceName()))
                .collect(Collectors.toList());


        ServiceDescription best = findCheapest(possibleServices, op);

        return PlanGenerator.retryWithService(best, op);
    }

    @Override
    public List<Plan> analyzeConfigurationChange() {
        if (areGoalsViolated()) {
            // threshold goal is violated load services with lowest failure rates
            return Arrays.asList(
                    PlanGenerator.reloadDefaults(),
                    PlanGenerator.loadSpecificServices(findServicesWithLowestFailureRates())
            );
        }


        // all is good reload defaults just to be sure
        return Collections.singletonList(PlanGenerator.reloadDefaults());
    }

    private boolean areGoalsViolated() {
        return !thresholdGoal.isSatisfied();
    }

    private List<ServiceDescription> findServicesWithLowestFailureRates() {
        // group by service types
        Map<String, List<ServiceDescription>> typeToSd = registry.getAllServices()
                .stream()
                .map(registry::getService)
                .collect(Collectors.groupingBy(ServiceDescription::getServiceType));

        // sort by failure rates
        typeToSd.values().forEach(
                lst -> lst.sort(Comparator.comparingDouble(sd -> (double) sd.getCustomProperties().get("FailureRate"))));

        // return with lowest
        return typeToSd.values().stream()
                .map(lst -> lst.get(0))
                .collect(Collectors.toList());
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
