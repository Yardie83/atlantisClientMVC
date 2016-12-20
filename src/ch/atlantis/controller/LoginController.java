package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Created by Hermann Grieder on 28.08.2016.
 * <p>
 * Controller for the Login View
 */
public class LoginController {

    final private AtlantisModel model;
    final private AtlantisView view;

    public LoginController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;
        handleLoginViewControls();
    }

    private void handleLoginViewControls() {
        //Attempt login on mouseClicked on the Login Button in the Login View
        view.getLoginView().getBtnLogin().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                attemptLogin();
            }
        });
        //Attempt login on Enter pressed in the Password TextField in the Login View
        view.getLoginView().getTxtPassword().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    attemptLogin();
                }
            }
        });

        //Switch to the new Profile View on mouseClicked on the Create Profile Button in the Login View
        view.getLoginView().getBtnCreateProfile().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getLoginView().getLblError().setText("");
                view.closeActiveOverlay();
                view.createNewProfileView();
                new NewProfileController(model, view);
                clearTextFields();
                view.getProfileStage().show();
            }
        });
        // Handle Cancel Button MouseClick Event in the Login View
        view.getLoginView().getBtnCancel().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getLoginView().getLblError().setText("");
                view.closeActiveOverlay();
                clearTextFields();
            }
        });
    }

    private void attemptLogin() {
        //TODO: Sanitize userInput by checking that username and password are not SQL statements
        //TODO: Sanitize input. Check for no commas in game name
        String userName = view.getLoginView().getTxtUserName().getText();
        String password = view.getLoginView().getTxtPassword().getText();
        String credentials = userName + "," + password;

        //Show the Error label when fields are left empty
        if (userName.equals("") || password.equals("")) {
            //view.getLoginView().getLblError().setText("Please fill in all fields");
            view.getLoginView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("login_lblError1"));
            view.getLoginView().getLblError().setVisible(true);
        } else {
            //Send the login credentials to the server
            model.sendMessage(new Message(MessageType.LOGIN, credentials));
        }

        model.loginSuccessProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (model.loginSuccessProperty().getValue().equals(1)) {
                            //view.getGameLobbyView().showPopUp("You're logged in!", 200);
                            view.getGameLobbyView().showPopUp(view.getSelectedLanguage().getLanguageTable().get("msgLoggedIn"), 200);
                            view.getGameLobbyView().removeLoginBtn();
                            view.closeActiveOverlay();
                            view.getGameLobbyView().getMenuItemInfo().setDisable(false);
                            model.loginSuccessProperty().setValue(0);
                            clearTextFields();
                        } else if (model.loginSuccessProperty().getValue().equals(2)) {
                            //view.getLoginView().getLblError().setText("Username or Password are wrong");
                            view.getLoginView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("login_lblError2"));
                            view.getLoginView().getLblError().setVisible(true);
                            model.loginSuccessProperty().setValue(0);
                        }
                    }
                });
            }
        });
    }

    private void clearTextFields(){
        this.view.getLoginView().getTxtUserName().setText("");
        this.view.getLoginView().getTxtPassword().setText("");
    }
}
