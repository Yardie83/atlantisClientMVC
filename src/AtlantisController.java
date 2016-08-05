import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 */

public class AtlantisController {

    final private AtlantisModel model;
    final private AtlantisView view;

    //Set debugMode to "true" in order to skip the intro video
    private boolean debugMode = true;

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
                closeApplication();
            }
        });
    }

    private void createGameLobby() {

        view.createGameLobbyView();
        model.connectToServer();

        view.getGameLobbyView().getBtnLogin().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createLoginView();
            }
        });

        view.getGameLobbyView().getBtnExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeApplication();
            }
        });

        /*
         CHAT: When the user presses enter the message is sent to the server
         */
        view.getGameLobbyView().getTxtField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    model.setAutoConnect(true);
                    TextField txtField = view.getGameLobbyView().getTxtField();
                    if (txtField.getText().equals("QUIT")) {
                        model.closeConnection();
                    } else {
                        model.sendMessage(new Message(MessageType.CHAT, txtField.getText()));
                    }
                    txtField.clear();
                }
            }
        });

        view.getGameLobbyView().getBtnOptions().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                view.createOptionsView();
            }
        });


        model.getChatString().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!(newValue.equals(""))) {
                    view.getGameLobbyView().getTxtArea().appendText("\n" + newValue);
                }
            }
        });

        model.getConnectionStatus().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                view.getGameLobbyView().getlblStatus().setText("Status: " + newValue);
            }
        });

    }

    private void closeApplication() {
        model.closeConnection();
        view.stop();
        System.exit(0);
    }
}