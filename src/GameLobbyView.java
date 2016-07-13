import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
public class GameLobbyView {

    public GameLobbyView(Stage primaryStage, MainController mainController) {
        VBox root = new VBox();

        Label testlbl = new Label("Game Lobby Scene");
        Button changeSceneBtn = new Button("Change Scene");

        changeSceneBtn.addEventHandler(ActionEvent.ACTION,mainController);

        root.getChildren().addAll(testlbl, changeSceneBtn);

        Scene gameLobbyScene = new Scene(root);

        primaryStage.setScene(gameLobbyScene);
    }
}
