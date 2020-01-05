package se.lnu.tas_system.tas.adaptation.assignment.t1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.tas_system.tas.adaptation.AdaptationEngine;

public class BasicAdaptationEngine implements AdaptationEngine {

    private static final Logger log = LoggerFactory.getLogger(BasicAdaptationEngine.class);

    @Override
    public void start() {
        log.info("Starting adaptation engine.");

    }

    @Override
    public void stop() {
        log.info("Stopping adaptation engine.");
    }
}
