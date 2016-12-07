package ch.atlantis;

import ch.atlantis.controller.AtlantisController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Date;
import java.util.logging.*;


/**
 * Created by Loris Grether and Hermann Grieder and Can Heval Cokyasar on 17.07.2016 / 29.11.2016.
 *
 * Start of the application.
 * Creates the model, the view, and the controller.
 *
 * Logger configuration.
 *
 */


public class AtlantisClient extends Application {

    // Name of the main logger
    private final static Logger LOGGER = Logger.getLogger(AtlantisClient.class.getName());

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage introStage) throws Exception {

        AtlantisModel model = new AtlantisModel();
        AtlantisView view = new AtlantisView(introStage, model);
        new AtlantisController(model, view);

        LOGGER.setLevel(Level.INFO);

        Handler fh = new FileHandler("log.txt");
        LOGGER.addHandler(fh);

        Handler ch = new ConsoleHandler();
        LOGGER.addHandler(ch);

        LogManager lm = LogManager.getLogManager();
        lm.addLogger(LOGGER);

    }
}
