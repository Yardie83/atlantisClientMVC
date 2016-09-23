package ch.atlantis.controller;

import ch.atlantis.game.Game;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.util.Collections;

/**
 * Created by Hermann Grieder on 28.08.2016.
 *
 */
public class GameLobbyController {

    final private AtlantisModel model;
    final private AtlantisView view;
    private final int HEIGHT = 800;
    private final int WIDTH = 1300;

    public GameLobbyController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;

        handleGameLobbyControls();
        addListeners();
    }

    /**
     * Handles all the input events for the controls in the GameLobby.
     */
    private void handleGameLobbyControls() {

        view.getGameLobbyView().getGameLobbyStage().setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.connectToServer();
            }
        });

        /*
         *Menu Bar Controls
         */

        view.getGameLobbyView().getMenuItemExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeApplication();
            }
        });

        //TODO: Does not work on top-level Menus. Need to add a submenu.
        view.getGameLobbyView().getMenuOptions().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createOptionsView(model.getLanguageList());
                new OptionsController(model, view);
            }
        });

        view.getGameLobbyView().getMenuItemGameRules().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.showGameRules();
            }
        });

        /*
         * Create Game, Login, Create Profile and Options Controls
         */

        // CREATE GAME Overlay
        view.getGameLobbyView().getBtnCreateGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createCreateGameView();
                new CreateGameController(model, view);
            }
        });
        // LOGIN Overlay
        view.getGameLobbyView().getBtnLogin().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createLoginView();
                new LoginController(model, view);
            }
        });
        // CREATE PROFILE Overlay
        view.getGameLobbyView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createNewProfileView();
                new NewProfileController(model, view);
            }
        });
        // OPTIONS Overlay
        view.getGameLobbyView().getBtnOptions().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createOptionsView(model.getLanguageList());
                new OptionsController(model, view);
            }
        });

        view.getGameLobbyView().getGameListView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        //  GAME Overlay
        view.getGameLobbyView().getBtnStartGame().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new Game(model, view);
            }
        });


        /*
         * CHAT Application EventHandlers
         */

        /*
         * When the user presses enter, the message is sent to the server
         */
        view.getGameLobbyView().getTxtField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    model.setAutoConnect(true);
                    TextField txtField = view.getGameLobbyView().getTxtField();
                    String username = model.userNameProperty().getValue();
                    String chatMessage = txtField.getText();
                    model.sendMessage(new Message(MessageType.CHAT, username + ": " + chatMessage));
                    txtField.clear();
                }
            }
        });


        // BUBBLES!!
        view.getGameLobbyView().getScene().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getGameLobbyView().createBubbles(event);

            }
        });

        // When the X Button is clicked, close the Application
        view.getGameLobbyView().getGameLobbyStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                closeApplication();
            }
        });
    }

    /**
     * Registers the different listeners to the gameLobbyController
     */
    private void addListeners() {

        view.getGameLobbyView().getGameLobbyStage().fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                view.setFullscreen(false);
                view.getGameLobbyView().getGameLobbyStage().setHeight(HEIGHT);
                view.getGameLobbyView().getGameLobbyStage().setWidth(WIDTH);
                view.bindSizeToStage();
                view.getGameLobbyView().bindSizeToStage();
            }
        });
    /* Incoming Message is saved in the ChatString. The changeListener listens to the ChatString
     * in order to update the txtArea with the incoming chat message.
     */
        //TODO: Ask Bradley if there is a better way instead of a ChangeListener. Because when the user enters the same message twice it does not register as a changed value
        model.getChatString().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!(newValue.equals(""))) {
                    view.getGameLobbyView().getTxtArea().appendText(newValue + "\n");
                }
            }
        });

        /*
         * STATUS and INFORMATION Bar EventHandlers (Bottom of the Game Lobby)
         */
        model.getConnectionStatus().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.getGameLobbyView().getLblStatus().setText("Status: " + newValue);
                    }
                });

            }
        });

        //UserName to be displayed. If the user is not logged in, Guest + number will be displayed as the name
        model.userNameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.getGameLobbyView().getLblWindowTitle().setText("Hi " + newValue + ", Welcome to Atlantis");
                        view.getGameLobbyView().getLblInfo().setText("Logged in as " + newValue);
                    }
                });
            }
        });

        //Update the GameList in the GameLobby with the List received from the Server
        model.getGameList().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (model.getGameList().size() != 0) {
                            view.getGameLobbyView().getGameListView().getItems().clear();
                            Collections.reverse(model.getGameList());
                            for (String s : model.getGameList()) {
                                String[] gameInfo = s.split(",");
                                if (!gameInfo[0].equalsIgnoreCase("")) {
                                    String gameName = gameInfo[0];
                                    Integer nrPlayers = Integer.parseInt(gameInfo[1]);
                                    view.getGameLobbyView().getGameListView().getItems().add(gameName + ": " + nrPlayers);
                                }
                            }
                            model.getGameList().clear();
                            view.getGameLobbyView().createPopUp("Game Created!", 200);
                        }
                    }
                });
            }
        });
    }

    /**
     * Closes the application, disconnects the client from the server and closes all the resources
     */
    private void closeApplication() {
        model.closeConnection();
        System.exit(0);
    }
}
