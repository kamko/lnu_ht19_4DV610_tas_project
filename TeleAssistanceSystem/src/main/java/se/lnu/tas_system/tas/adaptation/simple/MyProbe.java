package se.lnu.tas_system.tas.adaptation.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.WorkflowProbeInterface;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.tas_system.tas.adaptation.SimpleAdaptationEngine;

/**
 * @author M. Usman Iftikhar
 */
public class MyProbe implements WorkflowProbeInterface {

    private static final Logger log = LoggerFactory.getLogger(MyProbe.class);

    SimpleAdaptationEngine myAdaptationEngine;

    public void connect(SimpleAdaptationEngine myAdaptationEngine) {
        this.myAdaptationEngine = myAdaptationEngine;
    }

    @Override
    public void workflowStarted(String qosRequirement, Object[] params) {
        log.debug("MyProbe Workflow Started monitoring");
        //Log.addLog("WorkflowStarted", "Workflow Started monitoring");
    }

    @Override
    public void workflowEnded(Object result, String qosRequirement, Object[] params) {
        log.debug("MyProbe Workflow Ended");
    }

    @Override
    public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {

    }

    @Override
    public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {

    }

    @Override
    public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
        log.error("Service Failed: {}", service.getServiceName());

        // Remove service from cache
        myAdaptationEngine.handleServiceFailure(service, opName);
    }

    @Override
    public void serviceNotFound(String serviceType, String opName) {
        log.error("{}{} Not Found", serviceType, opName);
        myAdaptationEngine.handleServiceNotFound(serviceType, opName);
    }
}
