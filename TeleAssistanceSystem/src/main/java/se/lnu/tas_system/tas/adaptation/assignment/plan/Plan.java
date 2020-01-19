package se.lnu.tas_system.tas.adaptation.assignment.plan;

import java.util.Arrays;

public class Plan {
    private final AdaptationOption adaptation;
    private final Object[] args;

    Plan(AdaptationOption adaptation, Object... args) {
        this.adaptation = adaptation;
        this.args = args;
    }

    public AdaptationOption getAdaptation() {
        return adaptation;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "adaptation=" + adaptation +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
