package tas.adaptation.simple;

import service.adaptation.probes.interfaces.WorkflowProbeInterface;
import service.auxiliary.ServiceDescription;
import tas.adaptation.SimpleAdaptationEngine;

/**
 * 
 * @author M. Usman Iftikhar
 *
 */
public class MyProbe implements WorkflowProbeInterface {

	SimpleAdaptationEngine myAdaptationEngine;
		
	public void connect(SimpleAdaptationEngine myAdaptationEngine) {
	    this.myAdaptationEngine = myAdaptationEngine;
	}

	@Override
	public void workflowStarted(String qosRequirement, Object[] params) {
	    System.err.println("MyProbe Workflow Started monitoring");
	    //Log.addLog("WorkflowStarted", "Workflow Started monitoring");
	}

	@Override
	public void workflowEnded(Object result, String qosRequirement, Object[] params) {
	    System.err.println("MyProbe Workflow Ended");
	}

	@Override
	public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {

	}

	@Override
	public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {

	}

	@Override
	public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
	    System.err.println("Service Failed: " + service.getServiceName());
	    
	    // Remove service from cache
	    myAdaptationEngine.handleServiceFailure(service, opName);
	}
	
	@Override
	public void serviceNotFound(String serviceType, String opName){
	    System.err.println(serviceType + opName + "Not found");
	    myAdaptationEngine.handleServiceNotFound(serviceType, opName);
	}
}
