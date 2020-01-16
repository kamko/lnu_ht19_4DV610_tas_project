package se.lnu.tas_system.tas.adaptation.assignment.t1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.tas_system.tas.adaptation.AdaptationEngine;
import se.lnu.tas_system.tas.adaptation.assignment.knowledge.Watcher;
import se.lnu.tas_system.tas.services.assistance.AssistanceService;

public class BasicAdaptationEngine implements AdaptationEngine {

    private static final Logger log = LoggerFactory.getLogger(BasicAdaptationEngine.class);

    private final AssistanceService assistanceService;
    private final Watcher watcher = new Watcher();

    public BasicAdaptationEngine(AssistanceService assistanceService) {
        this.assistanceService = assistanceService;
    }

    @Override
    public void start() {
        log.info("Starting adaptation engine.");
        watcher.connectToService(assistanceService);

    }

    @Override
    public void stop() {
        log.info("Stopping adaptation engine.");
        watcher.disconnect(assistanceService);

    }

}
