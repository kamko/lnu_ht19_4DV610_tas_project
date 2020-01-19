package se.lnu.tas_system.tas.adaptation.assignment.t1;

import se.lnu.research_service_platform.service.adaptation.effectors.ConfigurationEffector;
import se.lnu.research_service_platform.service.adaptation.effectors.WorkflowEffector;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.tas_system.tas.adaptation.assignment.BasicAdaptationEngine;
import se.lnu.tas_system.tas.adaptation.assignment.analyze.Analyzer;
import se.lnu.tas_system.tas.adaptation.assignment.execute.PlanExecutor;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.Watcher;
import se.lnu.tas_system.tas.services.assistance.AssistanceService;

public class Task1AdaptationEngine extends BasicAdaptationEngine {

    public Task1AdaptationEngine(AssistanceService assistanceService, ServiceRegistry registry) {
        WorkflowEffector wfEffector = new WorkflowEffector(assistanceService);
        Watcher watcher = new Watcher();

        ConfigurationEffector cfgEffector = new ConfigurationEffector(assistanceService);
        PlanExecutor executor = new PlanExecutor(wfEffector, cfgEffector);
        Analyzer analyzer = new T1Analyzer(watcher.getKnowledge(), registry);

        super.init(assistanceService, watcher, analyzer, executor);
    }
}
