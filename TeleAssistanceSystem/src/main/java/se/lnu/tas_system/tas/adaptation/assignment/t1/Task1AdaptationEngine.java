package se.lnu.tas_system.tas.adaptation.assignment.t1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.effectors.ConfigurationEffector;
import se.lnu.research_service_platform.service.adaptation.effectors.WorkflowEffector;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.tas_system.tas.adaptation.assignment.ReactiveAdaptationEngine;
import se.lnu.tas_system.tas.adaptation.assignment.execute.PlanExecutor;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.Watcher;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;
import se.lnu.tas_system.tas.adaptation.assignment.plan.PlanGenerator;
import se.lnu.tas_system.tas.services.assistance.AssistanceService;

public class Task1AdaptationEngine implements ReactiveAdaptationEngine {

    private static final Logger log = LoggerFactory.getLogger(Task1AdaptationEngine.class);

    private final AssistanceService assistanceService;

    private final Watcher watcher = new Watcher();
    private final T1Analyzer t1Analyzer;
    private final PlanExecutor executor;

    private ConfigurationEffector cfgEffector;

    public Task1AdaptationEngine(AssistanceService assistanceService, ServiceRegistry registry) {
        this.assistanceService = assistanceService;

        WorkflowEffector wfEffector = new WorkflowEffector(assistanceService);
        this.cfgEffector = new ConfigurationEffector(assistanceService);
        this.executor = new PlanExecutor(wfEffector, cfgEffector);

        t1Analyzer = new T1Analyzer(watcher.getKnowledge(), registry);
    }

    @Override
    public void start() {
        log.info("Starting adaptation engine.");
        watcher.connect(this, assistanceService);
    }

    @Override
    public void stop() {
        log.info("Stopping adaptation engine.");
        watcher.disconnect(assistanceService);
        watcher.resetKnowledge();
    }

    @Override
    public void handleServiceFailure(ServiceDescription service, String opName, Object[] params) {
        log.error("handleServiceFailure(service={}, opName={}, params=...)", service, opName);

        Plan switchPlan = t1Analyzer.analyzeAfterFailure(service, opName);
        executor.execute(switchPlan);
    }

    @Override
    public void handleServiceNotFound(String serviceType, String opName) {
        executor.execute(PlanGenerator.planReload(serviceType, opName));
    }

    @Override
    public void handleWorkflowEnd(Object result, String qosRequirement, Object[] params) {
        this.cfgEffector.setMaxRetryAttempts(1);
        executor.execute(t1Analyzer.analyzeConfigurationChange());
    }
}
