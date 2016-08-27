package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.Collections;
import java.util.Random;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 */

public class AtlantisController {

    final private AtlantisModel model;
    final private AtlantisView view;

    private String userName;

    //Set debugMode to "true" in order to skip the intro video
    public final static boolean debugMode = true;

    public AtlantisController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;

        if (debugMode) {
            view.createGameLobbyView();
            System.out.println("DebugMode is on.\nIntro was skipped");
            handleGameLobbyControls();
            view.getGameLobbyView().show();
        } else {
            view.createIntroView();
            handleIntroViewControls();
        }
    }

    private void handleIntroViewControls() {

        view.getIntroStage().setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    view.getIntroView().getMediaPlayer().play();
                } catch (Exception e) {
                    System.err.println("Not able to play intro video");
                }
            }
        });

        view.getIntroStage().getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getIntroView().getMediaPlayer().stop();
                view.getIntroView().getMediaPlayer().dispose();
                view.getIntroStage().close();
                view.createGameLobbyView();
                handleGameLobbyControls();

                //Load and show the languages



                view.getGameLobbyView().show();
            }
        });
    }

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
         *Create ch.atlantis.game.Game, Login, Create Profile and Options Controls
         */
        view.getGameLobbyView().getBtnCreateGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createCreateGameView();
                handleCreateGameControls();
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

        view.getGameLobbyView().getGameListView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });


        /*
         * CHAT Application EventHandlers
         */

        /*
         * When the user presses enter, the message is sent to the server
         * If the user writes "Quit" the user is being disconnected from the chat
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

        /* Incoming ch.atlantis.util.Message is saved in the ChatString. Added this class "ch.atlantis.AtlantisController" as the changeListener of the ChatString
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
         * STATUS and INFORMATION Bar EventHandlers (Bottom of the ch.atlantis.game.Game Lobby)
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
         * UserName to be displayed. If the user is not logged in, Guest + number will be displayed as the name
         */
        model.userNameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                view.getGameLobbyView().getLblWindowTitle().setText("Hi " + newValue + ", Welcome to Atlantis");
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

        // BUBBLES!!
        view.getGameLobbyView().getScene().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        Random r = new Random();

                        for (int i = 0; i < 12; i++) {
                            Circle c = new Circle(r.nextInt(3) + 3, Color.SKYBLUE);
                            c.setStyle("-fx-border-color: WHITE;" +
                                    "-fx-border-width: 1px;" +
                                    "-fx-effect: dropshadow(gaussian, #bee1dc, 1, 0.3, -1, -1)");
                            c.setCenterX(event.getX() + r.nextInt(10) - 5);
                            c.setCenterY(event.getY() + r.nextInt(10));

                            view.getGameLobbyView().getChildren().add(c);

                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(r.nextInt(600) + 1400), c);
                            translateTransition.setFromX(0);
                            translateTransition.setToX(r.nextInt(40) - 20);
                            translateTransition.setFromY(0);
                            translateTransition.setToY(-r.nextInt(70) - 50);
                            translateTransition.setAutoReverse(false);

                            FadeTransition ft = new FadeTransition(Duration.millis(r.nextInt(600) + 1300), c);
                            ft.setFromValue(1);
                            ft.setToValue(0);
                            ft.setAutoReverse(false);

                            ParallelTransition parallelTransition = new ParallelTransition();
                            parallelTransition.getChildren().addAll(ft, translateTransition);
                            parallelTransition.setCycleCount(1);
                            parallelTransition.play();
                        }
                    }
                });
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

    private void handleCreateGameControls() {

        // Handle "Create" Btn Action Event in the Create ch.atlantis.game.Game View
        view.getCreateGameView().getBtnCreateNewGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createGameEntry();
            }
        });

        // Handle "Cancel" Btn Action Event in the Create ch.atlantis.game.Game View
        view.getCreateGameView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getCreateGameStage().close();
            }
        });

        view.getCreateGameView().getTxtGameName().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    createGameEntry();
                }
            }
        });
    }

    private void createGameEntry() {
        String gameName = view.getCreateGameView().getTxtGameName().getText();
        RadioButton selectedRadioButton = (RadioButton) view.getCreateGameView().getTgNoOfPlayers().getSelectedToggle();
        String message = gameName + "," + selectedRadioButton.getText();
        if (gameName.equals("")) {
            view.getCreateGameView().getLblError().setText("Please give your game a name");
            view.getCreateGameView().getLblError().setVisible(true);
        } else {
            model.sendMessage(new Message(MessageType.NEWGAME, message));
            view.getCreateGameStage().close();
        }

    }

    //END handleCreateGameControls

    private void handleLoginViewControls() {

        view.getLoginView().getBtnLogin().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO: Sanitize userInput by checking that username and password are not SQL statements
                //TODO: Sanitize input. Check for no commas in game name
                userName = view.getLoginView().getTxtUserName().getText();
                String password = view.getLoginView().getTxtPassword().getText();
                String credentials = userName + "," + password;

                //Show the Error label when fields are left empty
                if (userName.equals("") || password.equals("")) {
                    view.getLoginView().getLblError().setText("Please fill in all fields");
                    view.getLoginView().getLblError().setVisible(true);
                } else {
                    //Send the login credentials to the server
                    model.sendMessage(new Message(MessageType.LOGIN, credentials));
                }
            }
        });

        model.loginSuccessProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (model.loginSuccessProperty().getValue().equals(1)) {
                            view.getGameLobbyView().createPopUp("You're logged in!", 200);
                            view.getGameLobbyView().getLblInfo().setText("Logged in as " + userName);
                            view.getGameLobbyView().getLblWindowTitle().setText("Hi " + userName + ", Welcome to Atlantis");
                            view.getGameLobbyView().removeLoginBtn();
                            view.getLoginStage().close();
                            model.loginSuccessProperty().setValue(0);
                        } else if (model.loginSuccessProperty().getValue().equals(2)) {
                            view.getLoginView().getLblError().setText("Username or Password are wrong");
                            view.getLoginView().getLblError().setVisible(true);
                            model.loginSuccessProperty().setValue(0);
                        }
                    }
                });
            }

        });
        // Handle "Create Profile" Btn Action Event in the Login View
        view.getLoginView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.createNewProfileView();
                handleNewProfileControls();
                view.getLoginStage().close();
            }
        });
        // Handle "Cancel" Btn Action Event in the Login View
        view.getLoginView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getLoginStage().close();
            }
        });
    }
    //END handleLoginViewControls

    private void handleNewProfileControls() {

        // Handle Create Profile Btn Action Event in the create Profile View
        view.getNewProfileView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //TODO: Sanitize user input before sending it to the server.
                userName = view.getNewProfileView().getTxtUserName().getText();
                String password = view.getNewProfileView().getTxtPassword().getText();
                String passwordRevision = view.getNewProfileView().getTxtPasswordRevision().getText();

                if (userName.equals("") || password.equals("") || passwordRevision.equals("")) {
                    //Show the Error label when fields are left empty
                    view.getNewProfileView().getLblError().setText("Please fill in all fields");
                    view.getNewProfileView().getLblError().setVisible(true);
                } else if (!password.equals(passwordRevision)) {
                    //Show the Error label when the passwords do not match
                    view.getNewProfileView().getLblError().setText("Passwords do not match!");
                    view.getNewProfileView().getLblError().setVisible(true);
                } else {
                    // Send the UserName and the Password to the server to create the profile
                    String userInfo = userName + "," + password;
                    model.sendMessage(new Message(MessageType.CREATEPROFILE, userInfo));
                }
            }
        });
        // END of "Create Profile Btn" Functionality

        model.createProfileSuccessProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (model.createProfileSuccessProperty().getValue().equals(1)) {
                            view.getGameLobbyView().createPopUp("Profile Created!", 200);
                            view.getGameLobbyView().getLblInfo().setText("Logged in as " + userName);
                            view.getGameLobbyView().getLblWindowTitle().setText("Hi " + userName + ", Welcome to Atlantis");
                            view.getGameLobbyView().removeLoginBtn();
                            view.getProfileStage().close();
                            model.createProfileSuccessProperty().setValue(0);
                        } else if (model.createProfileSuccessProperty().getValue().equals(2)) {
                            view.getNewProfileView().getLblError().setText("Username already exists");
                            view.getNewProfileView().getLblError().setVisible(true);
                            model.createProfileSuccessProperty().setValue(0);
                        }
                    }
                });
            }
        });


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

                if (view.getOptionsView().getRadioBtnGerman().isSelected()){

                    //TODO: change language to german

                }
                else if (view.getOptionsView().getRadioBtnEnglish().isSelected()){

                    //TODO: change language to english

                }
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
        System.exit(0);
    }
}