package se.lnu.tas_system.tas.adaptation.assignment.plan;

public enum AdaptationOption {
    NO_ACTION,
    RETRY_WITH_EQUIVALENT_SERVICE,
    RELOAD_SERVICE_REGISTRY_FOR_OP,
    RELOAD_DEFAULT_SERVICES,
    LOAD_SPECIFIC_SERVICES,
    RESET_RETRY_ATTEMPTS
}
