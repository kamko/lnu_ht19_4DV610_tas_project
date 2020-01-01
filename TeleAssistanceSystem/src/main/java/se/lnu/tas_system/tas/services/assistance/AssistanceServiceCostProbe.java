/**
 *
 */
package se.lnu.tas_system.tas.services.assistance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.CostProbeInterface;
import se.lnu.research_service_platform.service.adaptation.probes.interfaces.WorkflowProbeInterface;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.research_service_platform.service.auxiliary.TimeOutError;
import se.lnu.research_service_platform.service.utility.SimClock;
import se.lnu.tas_system.tas.services.log.UILogger;

/**
 * @author yfruan
 * @email ry222ad@student.lnu.se
 *
 */
public class AssistanceServiceCostProbe implements WorkflowProbeInterface, CostProbeInterface {

    private static final Logger log = LoggerFactory.getLogger(AssistanceServiceCostProbe.class);

    private static String resultFilePath = "results" + File.separator + "result.csv";
    private double totalCost = 0;

    private StringBuilder resultBuilder;
    public int workflowInvocationCount = 0;
    private Map<String, Double> delays = new HashMap<>();


    static {
        File file = new File(resultFilePath);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    public void reset() {
        File file = new File(resultFilePath);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
        workflowInvocationCount = 0;
        totalCost = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see service.adaptation.Probe#workflowStarted(java.lang.String, java.lang.Object[])
     */
    @Override
    public void workflowStarted(String qosRequirement, Object[] params) {
        log.debug("Probe: workflowStarted");
        UILogger.addLog("WorkflowStarted", "Workflow Started monitoring");
        resultBuilder = new StringBuilder();
        totalCost = 0;
        workflowInvocationCount++;
    }

    /*
     * (non-Javadoc)
     *
     * @see service.adaptation.Probe#workflowEnded(java.lang.Object, java.lang.String, java.lang.Object[])
     */
    @Override
    public void workflowEnded(Object result, String qosRequirement, Object[] params) {
        log.debug("Probe: workflowEnded");
        if (result instanceof TimeOutError) {
            log.error("Workflow TimeoutError!");
            resultBuilder.append(workflowInvocationCount).append(",").append("AssistanceService").append(",false,").append(totalCost).append("\n");
        } else
            resultBuilder.append(workflowInvocationCount).append(",").append("AssistanceService").append(",true,").append(totalCost).append("\n");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(resultFilePath, true)))) {
            out.println(resultBuilder.toString());
        } catch (IOException e) {
            log.error("Error", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see service.adaptation.Probe#timeout(service.auxiliary.ServiceDescription, java.lang.String, java.lang.Object[])
     */
    @Override
    public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
        log.debug("Probe: timeout");
        resultBuilder.append(workflowInvocationCount).append(",").append(service.getServiceName()).append(",false\n");
    }

    @Override
    public void serviceCost(ServiceDescription service, String opName, double cost) {
        String serviceName = service.getServiceName();
        log.debug("Service Cost: {}", cost);
        String fullOperation = serviceName + "." + opName;


        Double begin = delays.get(fullOperation);
        //Double end=(Double)(System.nanoTime()/1000000.000);
        Double end = SimClock.getCurrentTime();

        totalCost = totalCost + cost;
        resultBuilder.append(workflowInvocationCount).append(",").append(serviceName).append(",true,").append(cost).append(",").append(begin).append(",").append(end - begin).append("\n");
    }

    @Override
    public void serviceOperationInvoked(ServiceDescription service,
                                        String opName, Object[] params) {
        // TODO Auto-generated method stub
        //delays.put(service.getServiceName()+"."+opName, (Double)(System.nanoTime()/1000000.000));
        delays.put(service.getServiceName() + "." + opName, SimClock.getCurrentTime());
    }

    @Override
    public void serviceOperationReturned(ServiceDescription service,
                                         Object result, String opName, Object[] params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void serviceNotFound(String serviceType, String opName) {
        // TODO Auto-generated method stub

    }
}
