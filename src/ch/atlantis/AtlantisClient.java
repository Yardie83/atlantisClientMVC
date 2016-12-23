package ch.atlantis;

import ch.atlantis.controller.AtlantisController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by Loris Grether, Hermann Grieder and Can Heval Cokyasar on 17.07.2016 / 29.11.2016.
 *
 * Start of the application.
 * Creates the model, the view, and the controller.
 *
 *
 * Logger configuration.
 *
 */


public class AtlantisClient extends Application {

    public static final String AtlantisLogger = AtlantisClient.class.getSimpleName();
    private Logger logger = null;
    private FileHandler fh = null;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage introStage) throws Exception {

        initLogger();

        AtlantisModel model = new AtlantisModel();
        AtlantisView view = new AtlantisView(introStage, model);
        new AtlantisController(model, view);

    }

    /**
     * Heval Cokyasar
     *
     * Sets up the logger
     */
    private void initLogger() {
        logger = Logger.getLogger(AtlantisLogger);

        try {

            // Configure logger with handler and formatter
            fh = new FileHandler("AtlantisClientLog.txt", 50000, 1);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();

            fh.setFormatter(formatter);

            logger.setLevel(Level.INFO);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
