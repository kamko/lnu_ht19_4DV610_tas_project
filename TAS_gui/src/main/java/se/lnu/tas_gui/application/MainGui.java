package se.lnu.tas_gui.application;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lnu.tas_gui.application.utility.Utility;
import se.lnu.tas_gui.application.view.controller.ApplicationController;
import se.lnu.tas_system.tas.services.log.UILogger;
import se.lnu.tas_system.tas.start.TASStart;

public class MainGui extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainGui.class);

    static final String logFile = "results" + File.separator + "log.csv";
    static final String resultFile = "results" + File.separator + "result.csv";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Utility.createFile(logFile);
        Utility.createFile(resultFile);
        UILogger.initialize(logFile);

        TASStart tasStart = new TASStart();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/javafx/application.fxml"));
        SplitPane pane = loader.load();

        ApplicationController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        controller.setTasStart(tasStart);
        controller.setCompositeService(tasStart.getAssistanceService());
        controller.setProbe(tasStart.getMonitor());
        controller.setConfigurations(tasStart.getAdaptationEngines());
        controller.setServiceRegistry(tasStart.getServiceRegistry());

        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/javafx/application.css").toExternalForm());
        primaryStage.setOnCloseRequest(arg0 -> {
            UILogger.stop();
            System.exit(0);
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tele Assistance System 1.61");
        primaryStage.show();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }

    public static void main(String[] args) {
        log.info("Starting application.");
        launch(args);
    }
}
