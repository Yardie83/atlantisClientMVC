import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class GameLobbyView {

    private ChatClient chatClient;

    private MainController mainController;
    private TextArea txtAreaChat;

    public GameLobbyView(Stage primaryStage, MainController mainController, ChatClient chatClient) throws IOException {

        this.mainController = mainController;
        this.chatClient = chatClient;

        VBox root = new VBox();

        Label lblTest = new Label("Game Lobby Scene");
        txtAreaChat = new TextArea();
        TextField txtChatMessage = new TextField();
        Button btnSend = new Button("Send");

        root.getChildren().addAll(lblTest, txtAreaChat, txtChatMessage, btnSend);

        Scene gameLobbyScene = new Scene(root);
        primaryStage.setScene(gameLobbyScene);


        /* Send Chat Message as String to the Controller */
        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    mainController.sendChatMessage(txtChatMessage.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void appendChatMessage(String chatText) {
        txtAreaChat.appendText(chatText + "/n");
    }
}
