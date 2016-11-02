package ch.atlantis;

import ch.atlantis.controller.AtlantisController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 * Start of the Application.
 * Creates the model, the view and the controller.
 */
public class AtlantisClient extends Application {

    public static final String LOGGER_NAME = AtlantisClient.class.getSimpleName();
    private Logger logger = null;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage introStage) throws Exception {

        logger = Logger.getLogger(LOGGER_NAME);
        logger.setLevel(Level.INFO);

        addLogHandler();

        AtlantisModel model = new AtlantisModel();
        AtlantisView view = new AtlantisView(introStage, model);
        new AtlantisController(model, view);

        logger.info("The Application was started");
    }

    private void addLogHandler() {
        try {
            //Handler logHandler = new FileHandler("%h/javaaaaaaaaaaaa%u.log", 50000, 3);
            //Handler logHandler = new FileHandler("%t/" + LOGGER_NAME + "_%u" + "_%g" + ".log", 1000000, 9);
            //logHandler.setLevel(Level.FINE);
            //logger.addHandler(logHandler);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize log files: " + e.toString());
        }
    }
}
