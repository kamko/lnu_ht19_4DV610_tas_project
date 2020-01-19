package se.lnu.tas_system.tas.adaptation.assignment.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.CostProbeInterface;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.WorkflowProbeInterface;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.auxiliary.ServiceFailed;
import se.lnu.research_service_platform.service.composite.CompositeService;
import se.lnu.tas_system.tas.adaptation.assignment.ReactiveAdaptationEngine;

public class Watcher implements WorkflowProbeInterface, CostProbeInterface {

    private static final Logger log = LoggerFactory.getLogger(Watcher.class);

    private Knowledge knowledge;
    private ReactiveAdaptationEngine engine;
    private CompositeService watchedService;

    public Watcher() {
        this.knowledge = new Knowledge();
    }

    public void connect(ReactiveAdaptationEngine engine, CompositeService service) {
        this.engine = engine;
        this.watchedService = service;
        service.getCostProbe().register(this);
        service.getWorkflowProbe().register(this);
    }

    public void disconnect(CompositeService service) {
        service.getCostProbe().unRegister(this);
        service.getWorkflowProbe().unRegister(this);
    }

    public void resetKnowledge() {
        knowledge = new Knowledge();
    }

    public Knowledge getKnowledge() {
        return knowledge;
    }

    @Override
    public void workflowStarted(String qosRequirement, Object[] params) {
        log.warn("workflowStarted(qosRequirement={}, params={})", qosRequirement, params);
    }

    @Override
    public void workflowEnded(Object result, String qosRequirement, Object[] params) {
        log.warn("workflowEnded(result={}, qosRequirement={}, params={}))", result, qosRequirement, params);

        handleResult(result);
        invokeIfEngineNotNull(() -> engine.handleWorkflowEnd(result, qosRequirement, params));
    }

    @Override
    public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {
        log.warn("serviceOperationInvoked(service={}, opName={}, params={})", service, opName, params);
        knowledge.markOperationInvocation(service, opName);
    }

    @Override
    public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {
        log.warn("serviceOperationReturned(service={}, result={}, opName={}, params={})", service, result, opName, params);
    }

    @Override
    public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
        log.warn("serviceOperationTimeout(service={}, opName={}, params={})", service, opName, params);
        knowledge.markOperationFailure(service, opName);
        invokeIfEngineNotNull(() -> engine.handleServiceFailure(service, opName, params));
    }

    @Override
    public void serviceNotFound(String serviceType, String opName) {
        log.warn("serviceOperationTimeout(serviceType={}, opName={})", serviceType, opName);
        invokeIfEngineNotNull(() -> engine.handleServiceNotFound(serviceType, opName));

    }

    @Override
    public void serviceCost(ServiceDescription description, String opName, double cost) {
        log.warn("serviceCost(description={}, opName={}, cost={})", description, opName, cost);
        knowledge.saveCost(description, opName, cost);
    }

    private void invokeIfEngineNotNull(Runnable r) {
        if (engine != null) {
            r.run();
        }
    }

    private void handleResult(Object result) {
        if (result instanceof ServiceFailed) {
            knowledge.markWorkflowFailure();
        } else {
            knowledge.markWorkflowSuccess();
        }
    }

}
