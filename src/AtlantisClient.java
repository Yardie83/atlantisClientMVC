import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class AtlantisClient extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new MainController(primaryStage);
    }
}
