import javafx.application.Application;
import javafx.stage.Stage;

import java.net.Socket;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 */
public class AtlantisClient extends Application {

    private AtlantisView view;
    private AtlantisModel model;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        model = new AtlantisModel();
        view = new AtlantisView(primaryStage, model);
        new AtlantisController(model, view);

        view.start();
    }

    @Override
    public void stop() {
        if (view != null)
            view.stop();
    }

    public void nothing(){}
}