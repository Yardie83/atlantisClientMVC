import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
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
                handleLoginViewControls();
            }
        });

        view.getGameLobbyView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createNewProfileView();
                handleNewProfileControls();
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

        //TODO: Better to create a Group node for all the buttons and then create the setOnMouseEntered and setOnMouseExited actions for that group

        // Change the Background color when the user hovers over the options button
        view.getGameLobbyView().getBtnOptions().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getGameLobbyView().getBtnOptions().setStyle("-fx-background-color: aqua");
            }
        });
        // Change the Background color when the user moves the mouse off the options button
        view.getGameLobbyView().getBtnOptions().setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getGameLobbyView().getBtnOptions().setStyle("-fx-background-color: azure");
            }
        });

        view.getGameLobbyView().getBtnOptions().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                view.createOptionsView();
            }
        });

        //TODO: Ask Bradley if there is a better way instead of a ChangeListener. Because when the user enters the same message twice it does not register as a changed value
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

    private void handleLoginViewControls() {

        view.getLoginView().getBtnLogin().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String username = view.getLoginView().getTxtUserName().getText();
                String password = view.getLoginView().getTxtPassword().getText();
                String credentials = username + "," + password;

                model.sendMessage(new Message(MessageType.LOGIN, credentials));
            }
        });

        view.getLoginView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getLoginStage().close();
            }
        });

        view.getLoginView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createNewProfileView();
                handleNewProfileControls();
                view.getLoginStage().close();
            }
        });

        //TODO: Play as Guest Button needs to be handled. But that means we need a game class and a player object which we don't have yet
    }

    private void handleNewProfileControls() {

        // Make the Profile Window Draggable
        final Double[] deltaX = new Double[1];
        final Double[] deltaY = new Double[1];

        view.getProfileStage().getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deltaX[0] = view.getProfileStage().getX() - event.getScreenX();
                deltaY[0] = view.getProfileStage().getY() - event.getScreenY();
            }
        });

        view.getProfileStage().getScene().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getProfileStage().setX(event.getScreenX() + deltaX[0]);
                view.getProfileStage().setY(event.getScreenY() + deltaY[0]);
            }
        });

        // Handle Create Profile Button
        view.getNewProfileView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO: Sanitize user input before sending it as a message to the server. Also check that username and password are not null or ""
                String userName = view.getNewProfileView().getTxtUserName().getText();
                String password = view.getNewProfileView().getTxtPassword().getText();
                String passwordRevision = view.getNewProfileView().getTxtPasswordRevision().getText();

                if (password.equals(passwordRevision)) {
                    String userInfo = userName + "," + password;
                    model.sendMessage(new Message(MessageType.CREATEPROFILE, userInfo));
                    view.getProfileStage().close();
                } else {
                    // Copied this from the internet. Could be a interesint alert alternative.
                    // Needs adjustments
//                    Dialog<Void> dialog = new Dialog<>();
//                    dialog.initModality(Modality.WINDOW_MODAL);
//                    dialog.initOwner(view.getProfileStage());//stage here is the stage of your webview
//                    dialog.initStyle(StageStyle.TRANSPARENT);
//                    Label loader = new Label("LOADING");
//                    loader.setContentDisplay(ContentDisplay.BOTTOM);
//                    loader.setGraphic(new ProgressIndicator());
//                    dialog.getDialogPane().setGraphic(loader);
//                    DropShadow ds = new DropShadow();
//                    ds.setOffsetX(1.3);
//                    ds.setOffsetY(1.3);
//                    ds.setColor(Color.DARKGRAY);
//                    dialog.getDialogPane().setEffect(ds);
//                    dialog.showAndWait();

                    //Alert Box when the password does not match
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Password does not match", ButtonType.OK);
                    alert.show();
                }
            }
        });
// Handle Cancel Profile creation Button
        view.getNewProfileView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getProfileStage().close();
            }

        });
    }

    private void closeApplication() {
        model.closeConnection();
        view.stop();
        System.exit(0);
    }
}