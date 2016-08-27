import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 */
public class AtlantisClient extends Application {

    private AtlantisView view;
    private AtlantisModel model;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage introStage) throws Exception {
        model = new AtlantisModel();
        view = new AtlantisView(introStage, model);
        new AtlantisController(model, view);

        if (!AtlantisController.debugMode) {
            view.getIntroStage().show();
        }
    }
}