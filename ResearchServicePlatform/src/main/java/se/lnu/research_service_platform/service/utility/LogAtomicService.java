/**
 *
 */
package se.lnu.research_service_platform.service.utility;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.service.atomic.ServiceProfile;

/**
 * @author Yifan Ruan (ry222ad@student.lnu.se)
 */
public class LogAtomicService extends ServiceProfile {

    private static final Logger log = LoggerFactory.getLogger(LogAtomicService.class);

    /**
     *
     */
    public boolean preInvokeOperation(String operationName, Object... args) {
        log.info("Operation {}", operationName);
        log.info("Arguments {}", Arrays.toString(args));
        return true;
    }

    /**
     *
     */
    public Object postInvokeOperation(String operationName, Object result, Object... args) {
        log.info("Operation: {}", operationName);
        log.info("Result: {}", result.toString());
        log.info("Arguments: {}", Arrays.toString(args));
        return result;
    }
}
