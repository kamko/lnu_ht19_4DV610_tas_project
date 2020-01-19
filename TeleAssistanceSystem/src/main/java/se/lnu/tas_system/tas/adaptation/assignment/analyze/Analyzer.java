package se.lnu.tas_system.tas.adaptation.assignment.analyze;

import java.util.List;

import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;
import se.lnu.tas_system.tas.adaptation.assignment.plan.Plan;

public interface Analyzer {
    Plan analyzeAfterFailure(ServiceDescription sd, String op);

    List<Plan> analyzeConfigurationChange();
}
