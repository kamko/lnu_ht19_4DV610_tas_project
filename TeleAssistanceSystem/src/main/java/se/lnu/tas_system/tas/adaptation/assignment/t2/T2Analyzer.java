package se.lnu.tas_system.tas.adaptation.assignment.t2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.tas_system.tas.adaptation.assignment.analyze.Analyzer;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;
import se.lnu.tas_system.tas.adaptation.assignment.plan.PlanGenerator;

public class T2Analyzer implements Analyzer {

    private static final Logger log = LoggerFactory.getLogger(T2Analyzer.class);

    private final ServiceRegistry registry;
    private final UtilityFunction utilityFunction;

    public T2Analyzer(ServiceRegistry registry) {
        this.registry = registry;

        utilityFunction = UtilityFunction.loadFromFile("results/utility-weights.csv");
    }

    @Override
    public Plan analyzeAfterFailure(ServiceDescription sd, String op) {
        log.info("Analyzing failure of service={} op={}", sd, op);
        List<ServiceDescription> possibleServices = registry.lookup(sd.getServiceType(), op)
                .stream()
                .filter(fsd -> !fsd.getServiceName().equals(sd.getServiceName()))
                .collect(Collectors.toList());


        ServiceDescription best = selectBest(possibleServices, op);

        return PlanGenerator.retryWithService(best, op);
    }

    @Override
    public List<Plan> analyzeConfigurationChange() {
        log.info("Loading services with best utility expectations.");
        return Arrays.asList(
                PlanGenerator.reloadDefaults(),
                PlanGenerator.loadSpecificServices(findServicesWithBestUtility())
        );
    }

    private List<ServiceDescription> findServicesWithBestUtility() {
        Map<String, List<ServiceDescription>> typeToSd = registry.getAllServices()
                .stream()
                .map(registry::getService)
                .collect(Collectors.groupingBy(ServiceDescription::getServiceType));

        return typeToSd.values().stream()
                .map(possible -> utilityFunction.findBest(possible,
                        s -> Arrays.asList(
                                (double) s.getCustomProperties().get("FailureRate"),
                                (double) s.getCustomProperties().get("Cost")))
                ).collect(Collectors.toList());
    }

    private ServiceDescription selectBest(List<ServiceDescription> services, String op) {
        return utilityFunction.findBest(
                services, s -> Arrays.asList(
                        (double) s.getCustomProperties().get("FailureRate"),
                        s.getOperationCost(op))
        );
    }
}
