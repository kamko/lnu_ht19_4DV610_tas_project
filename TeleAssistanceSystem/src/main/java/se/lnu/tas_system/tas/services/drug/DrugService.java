package se.lnu.tas_system.tas.services.drug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;

public class DrugService extends AtomicService {

    private static final Logger log = LoggerFactory.getLogger(DrugService.class);

    public DrugService(String serviceName, String serviceEndpoint) {
        super(serviceName, serviceEndpoint);
    }

    @ServiceOperation()
    public void changeDoses(int patientId) {
        log.info("Doses are changed.");
    }

    @ServiceOperation()
    public void changeDrug(int patientId) {
        log.info("Drug is changed.");
    }
}
