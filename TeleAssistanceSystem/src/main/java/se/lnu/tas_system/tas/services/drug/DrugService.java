package se.lnu.tas_system.tas.services.drug;

import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;

public class DrugService extends AtomicService {

    public DrugService(String serviceName, String serviceEndpoint) {
        super(serviceName, serviceEndpoint);
    }

    @ServiceOperation()
    public void changeDoses(int patientId) {
        System.out.println("Doses are changed.");
    }

    @ServiceOperation()
    public void changeDrug(int patientId) {
        System.out.println("Drug is changed.");
    }
}
