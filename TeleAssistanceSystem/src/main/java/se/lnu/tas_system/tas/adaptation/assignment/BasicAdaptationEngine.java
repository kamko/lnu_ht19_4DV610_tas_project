package se.lnu.tas_system.tas.adaptation.assignment;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.composite.CompositeService;
import se.lnu.tas_system.tas.adaptation.assignment.analyze.Analyzer;
import se.lnu.tas_system.tas.adaptation.assignment.execute.PlanExecutor;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.Watcher;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;
import se.lnu.tas_system.tas.adaptation.assignment.plan.PlanGenerator;

public abstract class BasicAdaptationEngine implements ReactiveAdaptationEngine {
    private static final Logger log = LoggerFactory.getLogger(se.lnu.tas_system.tas.adaptation.assignment.t1.Task1AdaptationEngine.class);

    private CompositeService compositeService;

    private Watcher watcher;
    private Analyzer analyzer;
    private PlanExecutor executor;

    protected void init(CompositeService compositeService,
                        Watcher watcher,
                        Analyzer analyzer,
                        PlanExecutor executor) {
        this.compositeService = compositeService;
        this.watcher = watcher;
        this.analyzer = analyzer;
        this.executor = executor;
    }

    @Override
    public void start() {
        log.info("Starting adaptation engine.");
        watcher.connect(this, compositeService);
    }

    @Override
    public void stop() {
        log.info("Stopping adaptation engine.");
        watcher.disconnect(compositeService);
        watcher.resetKnowledge();
    }

    @Override
    public void handleServiceFailure(ServiceDescription service, String opName, Object[] params) {
        log.error("handleServiceFailure(service={}, opName={}, params=...)", service, opName);

        Plan switchPlan = analyzer.analyzeAfterFailure(service, opName);
        executor.execute(switchPlan);
    }

    @Override
    public void handleServiceNotFound(String serviceType, String opName) {
        executor.execute(PlanGenerator.planReload(serviceType, opName));
    }

    @Override
    public void handleWorkflowEnd(Object result, String qosRequirement, Object[] params) {
        executor.execute(Stream.of(
                Collections.singletonList(PlanGenerator.resetRetryAttempts()),
                analyzer.analyzeConfigurationChange())
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        );
    }
}
