package ch.atlantis;

import ch.atlantis.controller.AtlantisController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 * Start of the Application.
 * Creates the model, the view and the controller.
 */
public class AtlantisClient extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage introStage) throws Exception {

        AtlantisModel model = new AtlantisModel();
        AtlantisView view = new AtlantisView(introStage, model);
        new AtlantisController(model, view);
    }
}
