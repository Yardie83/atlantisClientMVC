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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Collections;

/**
 * Created by Hermann Grieder on 28.08.2016.
 * <p>
 * Controller for the GameLobby.
 * Handles all events performed on the controls in the GameLobby
 * Registers itself to certain variables and listens to changed values
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
     * <p>
     * Hermann Grieder
     */

    private void handleGameLobbyControls() {

        Stage gameLobbyStage = view.getGameLobbyView().getGameLobbyStage();

        view.getGameLobbyView().getGameLobbyStage().setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                //Music.play();

                //model.soundController();

            }
        });

        /*
         * *******************************
         * Menu Bar Controls EventHandlers
         * *******************************
         */

        view.getGameLobbyView().getMenuItemExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeApplication();
            }
        });

        //TODO: Does not work on top-level Menus. Need to add a submenu or delete the whole thing
        view.getGameLobbyView().getMenuOptions().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createOptionsView(model.getLanguageList(), model.getConfigLanguage(),gameLobbyStage);
                new OptionsController(model, view);
                view.getOptionsStage().show();
            }
        });

        view.getGameLobbyView().getMenuItemGameRules().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.showGameRules();
            }
        });

        /*
         * *********************************************************************
         * Create Game, Login, Create Profile and Options Controls EventHandlers
         * *********************************************************************
         */

        // Create and show GAME Overlay
        view.getGameLobbyView().getBtnCreateGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createCreateGameView(gameLobbyStage);
                new CreateGameController(model, view);
                view.getCreateGameStage().show();
            }
        });
        // Create and show LOGIN Overlay
        view.getGameLobbyView().getBtnLogin().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createLoginView(gameLobbyStage);
                new LoginController(model, view);
                view.getLoginStage().show();
            }
        });
        // Create and Show PROFILE Overlay
        view.getGameLobbyView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createNewProfileView(gameLobbyStage);
                new NewProfileController(model, view);
                view.getProfileStage().show();
            }
        });
        // Create and show OPTIONS Overlay
        view.getGameLobbyView().getBtnOptions().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createOptionsView(model.getLanguageList(), model.getConfigLanguage(),gameLobbyStage);
                new OptionsController(model, view);
                view.getOptionsStage().show();
            }
        });
        // Adds the user to an existing game in the gameList on double-click
        view.getGameLobbyView().getGameListView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {

                    if (view.getGameLobbyView().getGameListView().getSelectionModel().getSelectedItem() != null) {

                        String listInfo = view.getGameLobbyView().getGameListView().getSelectionModel().getSelectedItem().toString();

                        model.joinGame(listInfo);
                    }
                }
            }
        });

        /*
         * ******************************
         * CHAT Application EventHandlers
         * ******************************
         */

        /*
         * When the user presses enter, the message is sent to the server
         *
         * Hermann Grieder
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

        /*
         * ******************************************************************
         * When the game is ready and the host clicks the visible start game button,
         * we send a message to the server that we want to receive the game information
         * needed to start the game.
         * ******************************************************************
         *
         * Hermann Grieder
         */

        view.getGameLobbyView().getStartGameBtn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.sendMessage(new Message(MessageType.STARTGAME));
            }
        });

        /*
         * ******************************************************************
         * BUBBLES!! It creates bubbles(!!) when you click in the gameLobby!!
         * ******************************************************************
         * Hermann Grieder
         */
        view.getGameLobbyView().getScene().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getGameLobbyView().createBubbles(event, 12);
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
     * Registers the different listeners
     * <p>
     * Hermann Grieder
     */

    private void addListeners() {

        /*
         * We have to listen for the change in the fullscreen property because when the
         * fullscreen flag gets set to true a stage does not have any width and height
         * values anymore for mysterious reasons.
         *
         * Hermann Grieder
         */

        view.getGameLobbyView().getGameLobbyStage().fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    view.setFullscreen(false);
                    view.getGameLobbyView().getGameLobbyStage().setHeight(HEIGHT);
                    view.getGameLobbyView().getGameLobbyStage().setWidth(WIDTH);
                    view.bindSizeToStage();
                    view.getGameLobbyView().bindSizeToStage();
                }
            }
        });

        /*
         * The incoming message is saved in the ChatString in the AtlantisModel class.
         * The changeListener listens to the ChatString in order to update the txtArea
         * with the incoming chat message.
         *
         * Hermann Grieder
         */

        model.getChatString().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                /*
                 * We have to remove the LocalDateTime substring which makes this string unique. The LocalDateTime
                 * has 23 characters and a space, so we want to show the message that starts after the 24th character.
                 * This is necessary because if we did not add a unique identifier like the LocalDateTime a user could
                 * not type the same message twice, since this changeListener would not register the second message as a
                 * newValue compared to the oldValue.
                 *
                 * Hermann Grieder
                 */

                String chatMessage = newValue.substring(24);
                view.getGameLobbyView().getTxtArea().appendText(chatMessage + "\n");
            }
        });

        /*
         * Sets the Status Label to either Connected or Disconnected, depending if the client
         * was able to connect to the server successfully
         *
         * Hermann Grieder
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

        /*
         * UserName to be displayed. If the user is not logged in,
         * Guest + number will be displayed as the name.
         *
         *  Hermann Grieder
         */

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

        /*
         * Updates the GameList ListView in the GameLobby with
         * the list received from the server
         *
         * Hermann Grieder
         */

        model.getGameList().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Only if the list is not empty go on
                        if (model.getGameList().size() != 0) {
                            // Clear the list first

                            view.getGameLobbyView().getGameListView().getItems().clear();

                            // Reverse the new list, so the newest games are on top
                            Collections.reverse(model.getGameList());

                            // Add each game in the gameList to the ListView
                            for (String s : model.getGameList()) {
                                String[] gameInfo = s.split(",");
                                if (!gameInfo[0].equalsIgnoreCase(" ")) {

                                    String gameName = gameInfo[0];
                                    Integer numberOfPlayers = Integer.parseInt(gameInfo[1]);
                                    int currentJoinedUsers = Integer.parseInt(gameInfo[2]);

                                    view.getGameLobbyView().getGameListView().getItems().add(
                                            gameName + " : " + currentJoinedUsers + " of " + numberOfPlayers + " players");
                                }
                            }
                            // Clear the list
                            model.getGameList().clear();
                            view.getGameLobbyView().createPopUp("Game Created!", 200);
                        }
                    }
                });
            }
        });


        model.gameReadyProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    view.getGameLobbyView().getStartGameBtn().setVisible(true);
                } else {
                    view.getGameLobbyView().getStartGameBtn().setVisible(false);
                }
            }
        });

        model.gameInfoProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue == Boolean.TRUE) {
                    Game g = new Game(model, view, model.getMessage(), model.getLocalPlayer());
                    g.showGame();
                    model.gameInfoProperty().setValue(false);
                }
            }
        });

    }

    /**
     * Closes the application, disconnects the client from the server and closes all resources
     * <p>
     * Hermann Grieder
     */

    private void closeApplication() {
        model.closeConnection();
        System.exit(0);
    }
}

