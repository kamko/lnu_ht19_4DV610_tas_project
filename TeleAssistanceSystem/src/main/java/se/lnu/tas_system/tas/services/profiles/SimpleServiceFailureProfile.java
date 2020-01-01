/**
 *
 */
package se.lnu.tas_system.tas.services.profiles;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.atomic.ServiceProfile;
import se.lnu.research_service_platform.service.atomic.ServiceProfileAttribute;

/**
 * @author yfruan
 * @email ry222ad@student.lnu.se
 */
public class SimpleServiceFailureProfile extends ServiceProfile {

    private static final Logger log = LoggerFactory.getLogger(SimpleServiceFailureProfile.class);

    @ServiceProfileAttribute()
    public double failureRate;   // failure number in 100 invocation times


    public SimpleServiceFailureProfile() {
    }

    public SimpleServiceFailureProfile(double failureRate) {
        this.failureRate = failureRate;
    }

    @Override
    public boolean preInvokeOperation(String operationName, Object... args) {
        log.debug("PreInvokeOperation");
        Random rand = new Random();
        return !(rand.nextDouble() <= failureRate);
    }

    @Override
    public Object postInvokeOperation(String operationName, Object result,
                                      Object... args) {
        return result;
    }
}
