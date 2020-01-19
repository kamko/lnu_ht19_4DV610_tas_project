package se.lnu.tas_system.tas.adaptation.assignment.execute;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.effectors.ConfigurationEffector;
import se.lnu.research_service_platform.service.adaptation.effectors.WorkflowEffector;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;

public class PlanExecutor {

    private static final Logger log = LoggerFactory.getLogger(PlanExecutor.class);

    private final WorkflowEffector wfEffector;
    private final ConfigurationEffector cfgEffector;


    public PlanExecutor(WorkflowEffector wfEffector, ConfigurationEffector cfgEffector) {
        this.wfEffector = wfEffector;
        this.cfgEffector = cfgEffector;
    }

    public void execute(Collection<Plan> plans) {
        plans.forEach(this::execute);
    }

    public void execute(Plan plan) {
        log.info("Executing adaptation plan {}", plan);

        switch (plan.getAdaptation()) {
            case RETRY_WITH_EQUIVALENT_SERVICE: {
                cfgEffector.addAnotherAttempt();

                String serviceName = (String) plan.getArgs()[0];
                String type = (String) plan.getArgs()[1];
                String op = (String) plan.getArgs()[2];

                wfEffector.setService(serviceName, type, op);
                break;
            }
            case RELOAD_SERVICE_REGISTRY: {
                String serviceType = (String) plan.getArgs()[0];
                String opName = (String) plan.getArgs()[1];
                wfEffector.refreshAllServices(serviceType, opName);
                break;
            }
            case NO_ACTION:
                break;
        }

    }
}
