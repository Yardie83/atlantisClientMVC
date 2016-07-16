import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class GameLobbyView {

    private MainController mainController;
    private final Connection connection;
    private TextArea txtAreaChat;
    private TextField txtChatMessage;
    public GameLobbyView(Stage primaryStage, MainController mainController, Connection connection) throws IOException {

        this.mainController = mainController;
        this.connection = connection;

        VBox root = new VBox();

        Label lblTest = new Label("Game Lobby Scene");
        txtAreaChat = new TextArea();
        txtAreaChat.setEditable(false);
        this.txtChatMessage = new TextField();
        Button btnSend = new Button("Send");
        appendChatMessage(connection.receiveMessages());
        root.getChildren().addAll(lblTest, txtAreaChat, txtChatMessage, btnSend);

        Scene gameLobbyScene = new Scene(root);
        primaryStage.setScene(gameLobbyScene);

        /* Send Chat Message as String to the Controller */
        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connection.send("CHAT" + txtChatMessage.getText().toString());
                txtChatMessage.clear();
            }
        });
    }

    public void appendChatMessage(String chatMessage) {
        txtAreaChat.appendText(chatMessage + "\n");
    }
}
