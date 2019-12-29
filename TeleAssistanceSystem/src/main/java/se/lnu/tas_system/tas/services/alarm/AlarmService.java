package se.lnu.tas_system.tas.services.alarm;

import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.auxiliary.ServiceOperation;

public class AlarmService extends AtomicService {

	public AlarmService(String serviceName, String serviceEndpoint) {
		super(serviceName, serviceEndpoint);
	}
	
	@ServiceOperation
	public void triggerAlarm(int patientId){
		System.out.println(this.getServiceDescription().getServiceName() + " is triggered!");
	}
}
