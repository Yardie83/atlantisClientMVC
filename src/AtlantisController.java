import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class AtlantisController {

    final private AtlantisModel model;
    final private AtlantisView view;

    public AtlantisController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;
        this.view.createIntroView();

        view.getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.createGameLobbyView();
                handleGameLobby();
                model.connectToServer();
            }
        });

        view.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                view.stop();
                Platform.exit();
            }
        });
    }

    private void handleGameLobby() {

        view.getGameLobbyView().getBtnExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.stop();
                Platform.exit();
            }
        });




    }
}