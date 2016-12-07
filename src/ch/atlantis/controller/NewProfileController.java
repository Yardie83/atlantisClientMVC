package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Created by Hermann Grieder on 28.08.2016.
 * <p>
 * New Profile View Controller
 */
public class NewProfileController {

    final private AtlantisModel model;
    final private AtlantisView view;

    public NewProfileController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;
        handleNewProfileControls();
    }

    private void handleNewProfileControls() {

        // Handle Create Profile Btn Action Event in the create Profile View
        view.getNewProfileView().getBtnCreateProfile().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //TODO: Sanitize user input before sending it to the server.
                String userName = view.getNewProfileView().getTxtUserName().getText();
                String password = view.getNewProfileView().getTxtPassword().getText();
                String passwordRevision = view.getNewProfileView().getTxtPasswordRevision().getText();

                if (userName.equals("") || password.equals("") || passwordRevision.equals("")) {
                    //Show the Error label when fields are left empty
                    //view.getNewProfileView().getLblError().setText("Please fill in all fields");
                    view.getNewProfileView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("login_lblError1"));

                    view.getNewProfileView().getLblError().setVisible(true);
                } else if (!password.equals(passwordRevision)) {
                    //Show the Error label when the passwords do not match
                    //view.getNewProfileView().getLblError().setText("Passwords do not match!");
                    view.getNewProfileView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("msgPasswordsNotMatch"));
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
                            //view.getGameLobbyView().showPopUp("Profile Created!", 200);
                            view.getGameLobbyView().showPopUp(view.getSelectedLanguage().getLanguageTable().get("msgProfileCreated"), 200);
                            view.getGameLobbyView().removeLoginBtn();
                            view.getNewProfileView().getLblError().setText("");
                            view.closeActiveOverlay();
                            model.createProfileSuccessProperty().setValue(0);
                        } else if (model.createProfileSuccessProperty().getValue().equals(2)) {
                            //view.getNewProfileView().getLblError().setText("Username already exists");
                            view.getNewProfileView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("msgUsernameExists"));
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
                view.getNewProfileView().getLblError().setText("");
                view.closeActiveOverlay();
            }
        });
    }
}

