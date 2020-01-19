package se.lnu.tas_system.tas.adaptation.assignment.execute;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.adaptation.effectors.ConfigurationEffector;
import se.lnu.research_service_platform.service.adaptation.effectors.WorkflowEffector;
import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
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
                int attempts = cfgEffector.addAnotherAttempt();

                if (attempts > 2) {
                    log.error("Only one retry allowed!");
                    cfgEffector.setMaxRetryAttempts(1);
                    break;
                }

                String serviceName = (String) plan.getArgs()[0];
                String type = (String) plan.getArgs()[1];
                String op = (String) plan.getArgs()[2];

                wfEffector.setService(serviceName, type, op);
                break;
            }
            case RELOAD_SERVICE_REGISTRY_FOR_OP: {
                String serviceType = (String) plan.getArgs()[0];
                String opName = (String) plan.getArgs()[1];
                wfEffector.refreshAllServices(serviceType, opName);
                break;
            }
            case RELOAD_DEFAULT_SERVICES: {
                wfEffector.refreshAllServices();
                break;
            }
            case LOAD_SPECIFIC_SERVICES: {
                @SuppressWarnings("unchecked")
                List<ServiceDescription> services = (List<ServiceDescription>) plan.getArgs()[0];
                wfEffector.addServices(services);
                break;
            }
            case RESET_RETRY_ATTEMPTS: {
                cfgEffector.setMaxRetryAttempts(1);
                break;
            }
            case NO_ACTION:
                break;
        }

    }
}
