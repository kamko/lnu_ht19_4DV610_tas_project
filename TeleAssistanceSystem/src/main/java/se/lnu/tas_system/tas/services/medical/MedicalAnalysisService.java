package se.lnu.tas_system.tas.services.medical;

import java.util.HashMap;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;

public class MedicalAnalysisService extends AtomicService {

    private static final Logger log = LoggerFactory.getLogger(MedicalAnalysisService.class);

    public MedicalAnalysisService(String serviceName, String serviceEndpoint) {
        super(serviceName, serviceEndpoint);
    }

    @ServiceOperation()
    public int analyzeData(HashMap data) {
        log.info("{} is started to analyze the data", getServiceDescription().getServiceName());
        return new Random().nextInt(3) + 1;
    }
}
