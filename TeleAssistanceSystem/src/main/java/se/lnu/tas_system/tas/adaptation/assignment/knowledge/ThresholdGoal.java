package se.lnu.tas_system.tas.adaptation.assignment.knowledge;

import java.util.function.Supplier;

public class ThresholdGoal {

    public enum Type {
        OVER, UNDER;
    }

    private final Type type;
    private final double threshold;
    private final Supplier<Double> valueSupplier;

    public ThresholdGoal(Type type, double threshold, Supplier<Double> valueSupplier) {
        this.type = type;
        this.threshold = threshold;
        this.valueSupplier = valueSupplier;
    }

    public boolean isSatisfied() {
        System.out.println(valueSupplier.get());
        switch (type) {
            case OVER:
                return valueSupplier.get() >= threshold;
            case UNDER:
                return valueSupplier.get() <= threshold;
        }

        return false;
    }
}
