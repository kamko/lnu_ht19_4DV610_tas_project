
package se.lnu.research_service_platform.service.adaptation.probes.interfaces;

import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;

/**
 * 
 *  Interface with list of function definitions for cost probe
 */
public interface CostProbeInterface {

	/**
	 * Probe the cost of a specific service
	 * @param serviceName the invoked service name
	 * @param opName   the invoked operation name
	 * @param cost   the invoked operation cost
	 */
    public void serviceCost(ServiceDescription description, String opName, double cost);
}
