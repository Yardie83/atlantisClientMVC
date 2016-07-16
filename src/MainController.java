import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class MainController {

    private Stage primaryStage;
    private Connection connection;

    public MainController(Stage primaryStage, Connection connection) throws IOException {

        this.primaryStage = primaryStage;
        this.connection = connection;
        configStage(primaryStage);
        new IntroView(primaryStage, this);
    }

    private void configStage(Stage primaryStage) {
        primaryStage.setTitle("Atlantis");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }


    public void createGameLobby() throws IOException {
        new GameLobbyView(primaryStage, this, connection);
    }
}
