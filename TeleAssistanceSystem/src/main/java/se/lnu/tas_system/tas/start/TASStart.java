package se.lnu.tas_system.tas.start;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.research_service_platform.profile.InputProfileValue;
import se.lnu.research_service_platform.profile.InputProfileVariable;
import se.lnu.research_service_platform.profile.ProfileExecutor;
import se.lnu.research_service_platform.service.adaptation.effectors.WorkflowEffector;
import se.lnu.research_service_platform.service.atomic.AtomicService;
import se.lnu.research_service_platform.service.composite.CompositeServiceClient;
import se.lnu.research_service_platform.service.registry.ServiceRegistry;
import se.lnu.research_service_platform.service.utility.Time;
import se.lnu.tas_system.tas.adaptation.AdaptationEngine;
import se.lnu.tas_system.tas.adaptation.DefaultAdaptationEngine;
import se.lnu.tas_system.tas.adaptation.SimpleAdaptationEngine;
import se.lnu.tas_system.tas.services.alarm.AlarmService;
import se.lnu.tas_system.tas.services.assistance.AssistanceService;
import se.lnu.tas_system.tas.services.assistance.AssistanceServiceCostProbe;
import se.lnu.tas_system.tas.services.drug.DrugService;
import se.lnu.tas_system.tas.services.medical.MedicalAnalysisService;
import se.lnu.tas_system.tas.services.profiles.ServiceDelayProfile;
import se.lnu.tas_system.tas.services.profiles.ServiceFailureProfile;
import se.lnu.tas_system.tas.services.qos.MinCostQoS;
import se.lnu.tas_system.tas.services.qos.PreferencesQoS;
import se.lnu.tas_system.tas.services.qos.ReliabilityQoS;

//This class was previously named as TASStart in the TAS verions 1.5 and 1.6.
public class TASStart {

    private static final Logger log = LoggerFactory.getLogger(TASStart.class);

    private HashMap<String, AdaptationEngine> adaptationEngines = new LinkedHashMap<>();

    protected ServiceRegistry serviceRegistry;
    protected AssistanceService assistanceService;
    protected AssistanceServiceCostProbe monitor;
    protected WorkflowEffector workflowEffector;

    protected AlarmService alarm1;
    protected AlarmService alarm2;
    protected AlarmService alarm3;

    protected MedicalAnalysisService medicalAnalysis1;
    protected MedicalAnalysisService medicalAnalysis2;
    protected MedicalAnalysisService medicalAnalysis3;

    protected DrugService drugService;

    private boolean isStopped = false;
    private boolean isPaused = false;
    private int currentSteps;

    private Map<String, AtomicService> atomicServices = new HashMap<>();
    private List<Class<?>> serviceProfileClasses = new ArrayList<>();

    private LinkedHashMap<String, String> serviceTypes = new LinkedHashMap<>();

    public synchronized void stop() {
        isStopped = true;
    }

    private synchronized void start() {
        isStopped = false;
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public synchronized void go() {
        isPaused = false;
        this.notifyAll();
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public void addAllServices(AtomicService... services) {
        for (AtomicService service : services) {
            atomicServices.put(service.getServiceDescription().getServiceName(), service);
            this.serviceTypes.put(service.getServiceDescription().getServiceName(),
                    service.getServiceDescription().getServiceType());
        }

    }

    public LinkedHashMap<String, String> getServiceTypes() {
        return this.serviceTypes;
    }

    public List<Class<?>> getServiceProfileClasses() {
        return this.serviceProfileClasses;
    }

    public AtomicService getService(String name) {
        return atomicServices.get(name);
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public AssistanceService getAssistanceService() {
        return assistanceService;
    }

    public AssistanceServiceCostProbe getMonitor() {
        return monitor;
    }

    public TASStart() {
        initializeTAS();
        adaptationEngines.put("No Adaptation", new DefaultAdaptationEngine());
        adaptationEngines.put("Simple Adaptation", new SimpleAdaptationEngine(assistanceService));
    }

    public HashMap<String, AdaptationEngine> getAdaptationEngines() {
        return adaptationEngines;
    }

    // Create and register various instances of the Alarm Service
    public void setupAlarmServices() {
        // ALarm Service 1
        alarm1 = new AlarmService("AlarmService1", "service.alarmService1");
        alarm1.getServiceDescription().getCustomProperties().put("Cost", 4.0);
        alarm1.getServiceDescription().getCustomProperties().put("preferred", true);
        alarm1.getServiceDescription().setOperationCost("triggerAlarm", 4.0);
        alarm1.getServiceDescription().getCustomProperties().put("FailureRate", 0.11);
        alarm1.addServiceProfile(new ServiceFailureProfile(0.11));
        alarm1.startService();
        alarm1.register();

        // ALarm Service 2
        alarm2 = new AlarmService("AlarmService2", "service.alarmService2");
        alarm2.getServiceDescription().getCustomProperties().put("Cost", 12.0);
        alarm2.getServiceDescription().setOperationCost("triggerAlarm", 12.0);
        alarm2.getServiceDescription().getCustomProperties().put("FailureRate", 0.04);
        alarm2.addServiceProfile(new ServiceFailureProfile(0.04));
        alarm2.startService();
        alarm2.register();

        // ALarm Service 3
        alarm3 = new AlarmService("AlarmService3", "service.alarmService3");
        alarm3.getServiceDescription().getCustomProperties().put("Cost", 2.0);
        alarm3.getServiceDescription().setOperationCost("triggerAlarm", 2.0);
        alarm3.getServiceDescription().getCustomProperties().put("FailureRate", 0.18);
        alarm3.addServiceProfile(new ServiceFailureProfile(0.18));
        alarm3.startService();
        alarm3.register();
    }

    // Create and register various instances of the Medical Service
    public void setupMedicalServices() {
        // Medical Analysis Service 1
        medicalAnalysis1 = new MedicalAnalysisService("MedicalService1", "service.medical1");
        medicalAnalysis1.getServiceDescription().getCustomProperties().put("preferred", true);
        medicalAnalysis1.getServiceDescription().getCustomProperties().put("Cost", 4.0);
        medicalAnalysis1.getServiceDescription().setOperationCost("analyzeData", 4.0);
        medicalAnalysis1.getServiceDescription().getCustomProperties().put("FailureRate", 0.12);
        medicalAnalysis1.addServiceProfile(new ServiceFailureProfile(0.02));
        medicalAnalysis1.startService();
        medicalAnalysis1.register();

        medicalAnalysis2 = new MedicalAnalysisService("MedicalService2", "service.medical2");
        medicalAnalysis2.getServiceDescription().getCustomProperties().put("Cost", 14.0);
        medicalAnalysis2.getServiceDescription().setOperationCost("analyzeData", 14.0);
        medicalAnalysis2.getServiceDescription().getCustomProperties().put("FailureRate", 0.07);
        medicalAnalysis2.addServiceProfile(new ServiceFailureProfile(0.07));
        medicalAnalysis2.startService();
        medicalAnalysis2.register();

        medicalAnalysis3 = new MedicalAnalysisService("MedicalService3", "service.medical3");
        medicalAnalysis3.getServiceDescription().getCustomProperties().put("Cost", 2.0);
        medicalAnalysis3.getServiceDescription().setOperationCost("analyzeData", 2.0);
        medicalAnalysis3.getServiceDescription().getCustomProperties().put("FailureRate", 0.18);
        medicalAnalysis3.addServiceProfile(new ServiceFailureProfile(0.18));
        medicalAnalysis3.startService();
        medicalAnalysis3.register();
    }

    // Create and register various instances of the Drug Service
    public void setupDrugServices() {
        // Drug Services
        drugService = new DrugService("DrugService", "service.drug");
        drugService.getServiceDescription().getCustomProperties().put("Cost", 5.0); // old was 2.0, changed to 5.0 because each underlying operation (changeDrug, and changeDoses costs 5.0
        drugService.getServiceDescription().setOperationCost("changeDoses", 5.0);
        drugService.getServiceDescription().setOperationCost("changeDrug", 5.0);
        drugService.getServiceDescription().getCustomProperties().put("FailureRate", 0.01);
        drugService.addServiceProfile(new ServiceFailureProfile(0.01));
        drugService.startService();
        drugService.register();
    }

    public void initializeTAS() {
        serviceRegistry = new ServiceRegistry();
        serviceRegistry.startService();

        //setup atomic services: Alarm, Medical and Drug Services
        setupAlarmServices();
        setupMedicalServices();
        setupDrugServices();

        // Assistance Service. Workflow is provided by TAS_gui through executeWorkflow method
        assistanceService = new AssistanceService("TeleAssistanceService", "service.assistance", "resources/TeleAssistanceWorkflow.txt");
        this.addAllServices(alarm1, alarm2, alarm3, medicalAnalysis1, medicalAnalysis2, medicalAnalysis3, drugService);

        //add QoS Requirements for the TAS services
        assistanceService.addQosRequirement("ReliabilityQoS", new ReliabilityQoS());
        assistanceService.addQosRequirement("PreferencesQoS", new PreferencesQoS());
        assistanceService.addQosRequirement("CostQoS", new MinCostQoS());

        //probes instrumentation
        monitor = new AssistanceServiceCostProbe();
        assistanceService.getCostProbe().register(monitor);
        assistanceService.getWorkflowProbe().register(monitor);
        //assistanceService.getWorkflowProbe().register(new AssistanceServiceDelayProbe());
//         assistanceService.getServiceInvocationProbe().register(monitor);

        //effectors instrumentation
        workflowEffector = new WorkflowEffector(assistanceService);

        this.serviceProfileClasses.add(ServiceFailureProfile.class);
        this.serviceProfileClasses.add(ServiceDelayProfile.class);
        //this.serviceProfileClasses.add(SimpleServiceDelayProfile.class);
        //this.serviceProfileClasses.add(SimpleServiceFailureProfile.class);

        assistanceService.startService();
        assistanceService.register();
    }

    public void stopServices() {
        serviceRegistry.stopService();

        alarm1.stopService();
        alarm2.stopService();
        alarm3.stopService();

        medicalAnalysis1.stopService();
        medicalAnalysis2.stopService();
        medicalAnalysis3.stopService();

        drugService.stopService();
        assistanceService.stopService();
    }

    public void executeWorkflow(String workflowPath, String profilePath) {
        CompositeServiceClient client = new CompositeServiceClient("service.assistance");
        assistanceService.setWorkflow(workflowPath);
        workflowEffector.refreshAllServices();
        Time.steps.set(0);

        ProfileExecutor.readFromXml(profilePath);
        if (ProfileExecutor.profile != null) {
            int maxSteps = (int) ProfileExecutor.profile.getMaxSteps();
            InputProfileVariable variable = ProfileExecutor.profile.getVariable("pick");
            List<InputProfileValue> values = variable.getValues();

            int patientId = (int) ProfileExecutor.profile.getVariable("patientId").getValues().get(0).getData();
            int pick;
            // System.out.println("start executing workflow !!!");

            start();
            Random rand = new Random();
            for (currentSteps = 0; currentSteps < maxSteps; currentSteps++) {

				/*
	    	synchronized(this){
	    		while(isPaused()){
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}*/

                //System.out.println(((ExecutionThread)Thread.currentThread()).getThreadName());

                Time.steps.incrementAndGet();

                double probability = rand.nextDouble();
                double valueProbability = 0;
                for (InputProfileValue value : values) {
                    if ((value.getRatio() + valueProbability) > probability) {
                        pick = (int) value.getData();
                        client.invokeCompositeService(ProfileExecutor.profile.getQosRequirement(), patientId, pick);
                        break;
                    } else
                        valueProbability = valueProbability + value.getRatio();
                }


                if (isStopped)
                    break;

            }
            stop();
            log.info("Workflow execution finished");
        }
    }

}
