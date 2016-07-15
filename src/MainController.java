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
    }

    private void configStage(Stage primaryStage){
        primaryStage.setTitle("Atlantis");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }

    @Override
    public void handle(ActionEvent event) {
        GameLobbyView gameLobbyView = new GameLobbyView(primaryStage, this);
        if(event.getEventType() == ActionEvent.ACTION){

        }
    }

    public void sendChatMessage(String text) throws IOException {
        chatClient.sendChatMessage();
        System.out.println("sendChatMessage Methode");
    }

    public void receiveChatMessage(String chatText) {
        gameLobbyView.appendChatMessage(chatText);
    }

}
