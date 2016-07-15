import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class MainController implements EventHandler<ActionEvent> {

    private GameLobbyView gameLobbyView;
    private Stage primaryStage;
    private ChatClient chatClient;


    public MainController(Stage primaryStage) throws IOException {

        this.primaryStage = primaryStage;
        configStage(primaryStage);
        new IntroView(primaryStage, this);
        chatClient = new ChatClient(this);

        // The method below needs to be somewhere else.
        // Also it does not work correctly
        //chatClient.run();

    }

    private void configStage(Stage primaryStage){
        primaryStage.setTitle("Atlantis");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            new GameLobbyView(primaryStage, this, chatClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(String text) throws IOException {
        chatClient.sendChatMessage(text);
    }

    public void receiveChatMessage(String chatText) {
        gameLobbyView.appendChatMessage(chatText);
    }

}
