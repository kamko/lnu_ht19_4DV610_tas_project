package se.lnu.tas_system.tas.adaptation.assignment.knowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.lnu.research_service_platform.service.auxiliary.ServiceDescription;

public class Knowledge {

    private List<ServiceDescription> inputConfiguration;
    private List<Boolean> workflowRuns = new ArrayList<>();
    private Map<String, ServiceStats> serviceStats = new HashMap<>();

    public int getWorkflowRuns() {
        return serviceStats.size();
    }

    public double getCompositeFailureRate() {
        int failures = workflowRuns.stream()
                .mapToInt(x -> x ? 0 : 1)
                .sum();

        return (1.0 * failures) / workflowRuns.size();
    }

    void markWorkflowFailure() {
        workflowRuns.add(false);
    }

    void markWorkflowSuccess() {
        workflowRuns.add(true);
    }

    void markOperationInvocation(ServiceDescription sd, String opName) {
        statsFor(sd).markInvocation(opName);
    }

    void markOperationFailure(ServiceDescription sd, String opName) {
        statsFor(sd).markFailure(opName);
    }

    void saveCost(ServiceDescription sd, String op, double cost) {
        statsFor(sd).opStatsFor(op).addCost(cost);
    }

    private ServiceStats statsFor(ServiceDescription sd) {
        return statsFor(sd.getServiceName());
    }

    private ServiceStats statsFor(String name) {
        return serviceStats.computeIfAbsent(name, s -> new ServiceStats(name));
    }

    public static class ServiceStats {

        private final String name;

        private final Map<String, OpStats> opStats = new HashMap<>();

        private int invocations = 0;
        private int failures = 0;

        public ServiceStats(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        double getFailureRate() {
            return (failures * 1.0) / invocations;
        }

        double getTotalCost() {
            return opStats.values().stream()
                    .mapToDouble(os -> os.cost)
                    .sum();
        }

        void markInvocation(String op) {
            invocations++;
            opStatsFor(op).markInvocation();
        }

        void markFailure(String op) {
            failures++;
            opStatsFor(op).markFailure();
        }

        private OpStats opStatsFor(String opName) {
            return opStats.computeIfAbsent(opName, s -> new OpStats());
        }
    }

    public static class OpStats {
        private int invocations = 0;
        private int failures = 0;
        private double cost = 0;

        void addCost(double cost) {
            this.cost += cost;
        }

        void markInvocation() {
            invocations++;
        }

        void markFailure() {
            failures++;
        }
    }
}
