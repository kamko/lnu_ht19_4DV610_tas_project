package se.lnu.tas_system.tas.services.assistance;

import java.util.HashMap;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.auxiliary.CompositeServiceConfiguration;
import se.lnu.research_service_platform.service.auxiliary.LocalOperation;
import se.lnu.research_service_platform.service.composite.CompositeService;

@CompositeServiceConfiguration(
        SDCacheMode = true
        //,Timeout = 1
        //,MaxRetryAttempts = 3
)

public class AssistanceService extends CompositeService {

    private static final Logger log = LoggerFactory.getLogger(AssistanceService.class);

    public AssistanceService(String serviceName, String serviceEndpoint,
                             String workflowFilePath) {
        super(serviceName, serviceEndpoint, workflowFilePath);
    }


    @LocalOperation
    public int pickTask() {

        log.info("Pick (1) to measure vital parameters, (2) for emergency and (3) to stop service.");

        //return 2;

        Scanner in = new Scanner(System.in);
        do {
            String line = in.nextLine();
            int pick = Integer.parseInt(line);
            if (pick < 1 || pick > 3) {
                log.error("Wrong value {}", pick);
            } else {
                return pick;
            }
        } while (true);
    }


    @LocalOperation
    public HashMap getVitalParameters() {
        return new HashMap();
    }
}
