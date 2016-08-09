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

import java.util.ArrayList;

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

        /*
         *Menu Bar Controls
         */
        view.getGameLobbyView().getMenuItemExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeApplication();
            }
        });

        //TODO: Does not work for some reason
        view.getGameLobbyView().getMenuOptions().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createOptionsView();
                handleOptionsControls();
            }
        });

        view.getGameLobbyView().getMenuItemGameRules().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.showGameRules();
            }
        });

        /*
         *Create Game, Login, Create Profile and Options Controls
         */
        view.getGameLobbyView().getBtnCreateGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createCreateGameView();
                handleCreateGameControls();
                ArrayList<Integer> testList = new ArrayList<Integer>();
                testList.add(2);
                testList.add(5);
                testList.add(4);
                model.sendMessage(new Message(MessageType.LOGIN, testList));
            }
        });

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

        view.getGameLobbyView().getBtnOptions().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createOptionsView();
                handleOptionsControls();
            }
        });


        /*
         * CHAT Application EventHandlers
         */

        /*
         * When the user presses enter the message is sent to the server
         */
        view.getGameLobbyView().getTxtField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    model.setAutoConnect(true);
                    TextField txtField = view.getGameLobbyView().getTxtField();
                    if (txtField.getText().equalsIgnoreCase("QUIT")) {
                        model.closeConnection();
                    } else {
                        model.sendMessage(new Message(MessageType.CHAT, txtField.getText()));
                    }
                    txtField.clear();
                }
            }
        });

        /* Incoming Message is saved in the ChatString. Added this class as the changeListener of the ChatString
         * in order to update the txtArea with the incoming chat message.
         */
        //TODO: Ask Bradley if there is a better way instead of a ChangeListener. Because when the user enters the same message twice it does not register as a changed value
        model.getChatString().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!(newValue.equals(""))) {
                    view.getGameLobbyView().getTxtArea().appendText("\n" + newValue);
                }
            }
        });

        /*
         * STATUS and INFORMATION Bar EventHandlers (Bottom of the Game Lobby)
         */
        model.getConnectionStatus().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                view.getGameLobbyView().getLblStatus().setText("Status: " + newValue);
            }
        });
    }

    private void handleCreateGameControls() {


        // Handle Create Btn Action Event in the Create Game View
        view.getCreateGameView().getBtnCreateNewGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO: Handle the creation of a game here
                view.getCreateGameStage().close();
            }
        });

        // Handle Cancel Btn Action Event in the Create Game View
        view.getCreateGameView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getCreateGameStage().close();
            }
        });

    }
    //END handleCreateGameControls

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
    //END handleLoginViewControls

    private void handleNewProfileControls() {

        // Handle Create Profile Btn Action Event in the create Profile View
        view.getNewProfileView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //TODO: Sanitize user input before sending it as a message to the server. Also check that username and password are not null or ""
                String userName = view.getNewProfileView().getTxtUserName().getText();
                String password = view.getNewProfileView().getTxtPassword().getText();
                String passwordRevision = view.getNewProfileView().getTxtPasswordRevision().getText();

                // Send the UserName and the Password to the server to create the profile
                if (password.equals(passwordRevision)) {
                    String userInfo = userName + "," + password;
                    model.sendMessage(new Message(MessageType.CREATEPROFILE, userInfo));
                    view.getProfileStage().close();
                } else {
                    //Alert Box when the Passwords do not match
                    //TODO: Should not be an alert box but a Label that shows up in red and tells the user the password or username was wrong.
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Password does not match", ButtonType.OK);
                    alert.show();
                }
            }
        });
        // END of "Create Profile Btn" Functionality

        // Handle Cancel Btn Action Event in the create Profile View
        view.getNewProfileView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getProfileStage().close();
            }
        });
    }
    //END "Cancel Btn" Functionality
    //END handleNewProfileControls method

    // Handle Options Controls' Action Events in the Options View
    private void handleOptionsControls() {
        view.getOptionsView().getBtnApply().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            //TODO: Options Apply Button needs to be handled
            }
        });

        view.getOptionsView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getOptionsStage().close();
            }
        });
    }
    //END Handle Options Controls

    private void closeApplication() {
        model.closeConnection();
        view.stop();
        System.exit(0);
    }
}