import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import java.net.Socket;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class MainController implements EventHandler<ActionEvent> {

    private Socket clientSocket;
    private final String HOST = "127.0.0.1";
    private final int PORT = 9000;

    private Stage primaryStage;


    public MainController(Stage primaryStage) {

        this.primaryStage = primaryStage;

        connectToServer();
        configStage(primaryStage);

        new IntroView(primaryStage, this);
    }

    private void connectToServer() {

        System.out.println("Connecting to Server...");

        System.out.println("Connection successful!");
    }

    private void configStage(Stage primaryStage){
        primaryStage.setTitle("Atlantis");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }

    @Override
    public void handle(ActionEvent event) {
        new GameLobbyView(primaryStage, this);
    }
}
