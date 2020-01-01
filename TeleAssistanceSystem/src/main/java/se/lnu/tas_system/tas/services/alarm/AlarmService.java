package se.lnu.tas_system.tas.services.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;

public class AlarmService extends AtomicService {

    private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

    public AlarmService(String serviceName, String serviceEndpoint) {
        super(serviceName, serviceEndpoint);
    }

    @ServiceOperation
    public void triggerAlarm(int patientId) {
        log.debug("{} is triggered!", getServiceDescription().getServiceName());
    }
}
