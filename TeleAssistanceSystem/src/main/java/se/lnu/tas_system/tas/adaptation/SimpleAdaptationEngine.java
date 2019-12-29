/**
 *
 */

package se.lnu.tas_system.tas.adaptation;

import se.lnu.research_service_platform.service.adaptation.effectors.WorkflowEffector;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.tas_system.tas.adaptation.simple.MyProbe;
import se.lnu.tas_system.tas.services.assistance.AssistanceService;

/**
 * @author M. Usman Iftikhar
 * @email muusaa@lnu.se
 *
 */
public class SimpleAdaptationEngine implements AdaptationEngine {

    MyProbe myProbe;
    WorkflowEffector myEffector;
    AssistanceService assistanceService;

    public SimpleAdaptationEngine(AssistanceService assistanceService) {
        this.assistanceService = assistanceService;
        myProbe = new MyProbe();
        myProbe.connect(this);
        myEffector = new WorkflowEffector(assistanceService);
    }


    public void handleServiceFailure(ServiceDescription service, String opName) {
        this.myEffector.removeService(service);
    }

    public void handleServiceNotFound(String serviceType, String opName) {
        myEffector.refreshAllServices(serviceType, opName);
    }

    @Override
    public void start() {
        assistanceService.getWorkflowProbe().register(myProbe);
        myEffector.refreshAllServices();
    }

    @Override
    public void stop() {
        assistanceService.getWorkflowProbe().unRegister(myProbe);
    }
}
