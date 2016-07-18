import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */

public class AtlantisController {

    final private AtlantisModel model;
    final private AtlantisView view;
    private boolean debugMode = false;

    public AtlantisController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;

        if (debugMode) {
            view.createGameLobbyView();
        } else {
            view.createIntroView();
        }


        view.getStage().setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (debugMode) {
                    System.out.println("DebugMode is on. Intro was skipped");
                    createGameLobby();
                } else {
                    try {
                        view.getIntroView().getMediaPlayer().play();
                    } catch (Exception e) {
                        System.err.println("Not able to play intro video");
                    }
                }
            }
        });

        view.getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getIntroView().getMediaPlayer().stop();
                view.getIntroView().getMediaPlayer().dispose();
                createGameLobby();
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

    private void createGameLobby() {

        view.createGameLobbyView();
        model.connectToServer();

        view.getGameLobbyView().getBtnExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.stop();
                Platform.exit();
            }
        });

        view.getGameLobbyView().getTxtField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    TextField txtField = view.getGameLobbyView().getTxtField();
                    model.sendMessage(txtField.getText());
                    txtField.clear();
                }
            }
        });
    }
}