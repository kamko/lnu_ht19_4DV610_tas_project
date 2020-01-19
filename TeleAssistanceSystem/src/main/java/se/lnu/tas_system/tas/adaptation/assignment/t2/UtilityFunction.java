package se.lnu.tas_system.tas.adaptation.assignment.t2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class UtilityFunction {

    enum Type {
        MAX, MIN
    }

    private Type type;
    private List<Double> weights = new ArrayList<>();

    public static UtilityFunction loadFromFile(String path) {
        try {
            String data = new String(Files.readAllBytes(Paths.get(path)));
            String[] splitData = data.split(",");

            UtilityFunction res = new UtilityFunction();
            for (int i = 0; i < splitData.length; i++) {
                if (i == 0) {
                    res.type = Type.valueOf(splitData[0]);
                } else {
                    res.weights.add(Double.parseDouble(splitData[i]));
                }
            }

            return res;
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public <T> double calculate(T t, Function<T, List<Double>> valuesExtractor) {
        return calculate(valuesExtractor.apply(t));
    }

    public double calculate(List<Double> values) {
        if (values.size() != weights.size()) {
            throw new RuntimeException("Invalid values count");
        }

        double sum = 0.0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i) * weights.get(i);
        }

        return sum;
    }

    public <T> T findBest(List<T> possibilities, Function<T, List<Double>> valuesExtractor) {
        List<T> posCopy = new ArrayList<>(possibilities);

        posCopy.sort(Comparator.comparingDouble(t -> calculate(t, valuesExtractor)));

        if (type == Type.MAX) {
            return posCopy.get(posCopy.size() - 1);
        } else {
            return posCopy.get(0);
        }
    }
}
