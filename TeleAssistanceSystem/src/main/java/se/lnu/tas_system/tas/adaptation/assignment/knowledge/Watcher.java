package se.lnu.tas_system.tas.adaptation.assignment.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.CostProbeInterface;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.WorkflowProbeInterface;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.composite.CompositeService;

public class Watcher implements WorkflowProbeInterface, CostProbeInterface {

    private static final Logger log = LoggerFactory.getLogger(Watcher.class);

    public void connectToService(CompositeService service) {
        service.getCostProbe().register(this);
        service.getWorkflowProbe().register(this);
    }

    public void disconnect(CompositeService service) {
        service.getCostProbe().unRegister(this);
        service.getWorkflowProbe().unRegister(this);
    }

    @Override
    public void workflowStarted(String qosRequirement, Object[] params) {
        log.warn("workflowStarted(qosRequirement={}, params={})", qosRequirement, params);
    }

    @Override
    public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {
        log.warn("serviceOperationInvoked(service={}, opName={}, params={})", service, opName, params);
    }

    @Override
    public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {
        log.warn("serviceOperationReturned(service={}, result={}, opName={}, params={})", service, result, opName, params);
    }

    @Override
    public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
        log.warn("serviceOperationTimeout(service={}, opName={}, params={})", service, opName, params);
    }

    @Override
    public void serviceNotFound(String serviceType, String opName) {
        log.warn("serviceOperationTimeout(serviceType={}, opName={})", serviceType, opName);
    }

    @Override
    public void workflowEnded(Object result, String qosRequirement, Object[] params) {
        log.warn("workflowEnded(result={}, qosRequirement={}, params={}))", result, qosRequirement, params);
    }

    @Override
    public void serviceCost(ServiceDescription description, String opName, double cost) {
        log.warn("serviceCost(description={}, opName={}, cost={})", description, opName, cost);
    }

}
