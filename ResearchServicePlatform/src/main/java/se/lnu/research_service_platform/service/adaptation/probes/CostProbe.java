package se.lnu.research_service_platform.service.adaptation.probes;

import se.lnu.research_service_platform.service.adaptation.probes.interfaces.CostProbeInterface;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;

/**
 * Monitor the cost of service invocations
 */
public class CostProbe extends AbstractProbe<CostProbeInterface> {

    /**
     * Notify subscribed probes the cost of a service
     *
     * @param description the invoked service description
     * @param opName      the invoked operation name
     */
    public void notifyCostSubscribers(ServiceDescription description, String opName) {
        double cost = description.getOperationCost(opName);
        //String serviceName=description.getServiceName();
        for (CostProbeInterface subscriber : subscribers) {
            subscriber.serviceCost(description, opName, cost);
        }
    }

}
