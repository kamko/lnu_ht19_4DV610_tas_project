package se.lnu.tas_system.tas.services.profiles;

import java.util.Iterator;
import java.util.TreeMap;

import se.lnu.research_service_platform.service.atomic.ServiceProfile;
import se.lnu.research_service_platform.service.atomic.ServiceProfileAttribute;
import se.lnu.research_service_platform.service.utility.SimClock;
import se.lnu.research_service_platform.service.utility.Time;

public class ServiceDelayProfile extends ServiceProfile {

    @ServiceProfileAttribute()
    public TreeMap<Integer, Double> delay = new TreeMap<>();


    public ServiceDelayProfile() {
        delay.put(0, 0.0);
    }

    public ServiceDelayProfile(double value) {
        delay.put(0, value);
    }

    @Override
    public boolean preInvokeOperation(String operationName, Object... args) {

        int invocationNum = Time.steps.get();
        double delayValue = 0;

        Iterator<Integer> iter = delay.keySet().iterator();
        int num;
        while (iter.hasNext()) {
            num = iter.next();
            if (invocationNum >= num)
                delayValue = delay.get(num);
            if (invocationNum < num)
                break;
        }
		
		/*
	    try {
			Thread.sleep(delayValue);
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }*/

        SimClock.increment(delayValue);

        return true;
    }

    @Override
    public Object postInvokeOperation(String operationName, Object result,
                                      Object... args) {
        return result;
    }
}
