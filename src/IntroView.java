import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Hermann Grieder on 13.07.2016.
 *
 */
class IntroView {

    IntroView(Stage primaryStage, MainController mainController) {

        VBox root = new VBox();

        Label testlbl = new Label("Test");
        Button changeSceneBtn = new Button("Change Scene");

        changeSceneBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                try {
                    mainController.createGameLobby();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        root.getChildren().addAll(testlbl, changeSceneBtn);

        Scene introScene = new Scene(root);

        primaryStage.setScene(introScene);
        primaryStage.show();
    }
}
