package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by Hermann Grieder on 28.08.2016.
 * <p>
 * Handles all the controls in the create game overlay. Checks if fields are empty,
 */
public class CreateGameController {

    final private AtlantisModel model;
    final private AtlantisView view;

    public CreateGameController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;
        handleCreateGameControls();
    }

    private void handleCreateGameControls() {

        view.getCreateGameStage().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    view.getCreateGameView().getLblError().setText("");
                    view.closeActiveOverlay();
                }
            }
        });

        // Handle "Create" Btn Action Event in the Create Game View
        view.getCreateGameView().getBtnCreateNewGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameCreated()) {
                    cleanUp();
                }
            }
        });
        // The user can also press the ENTER key to create a game in the Create Game View
        view.getCreateGameView().getTxtGameName().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (gameCreated()) {
                        cleanUp();
                    }
                }
            }
        });

        // Handle "Cancel" Btn Action Event in the Create Game View
        view.getCreateGameView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getCreateGameView().getLblError().setText("");
                view.closeActiveOverlay();
                clearTextFields();
            }
        });
    }


    /**
     * Hermann Grieder
     * <br>
     * Checks that the gameName is not empty and sends the gameName and the number of players to the server.
     *
     * @return
     */
    private boolean gameCreated() {
        String gameName = view.getCreateGameView().getTxtGameName().getText();
        RadioButton selectedRadioButton = (RadioButton) view.getCreateGameView().getTgNoOfPlayers().getSelectedToggle();
        String message = gameName + "," + selectedRadioButton.getText();
        if (gameName.equals("")) {
            view.getCreateGameView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("msgGiveGameName"));
            view.getCreateGameView().getLblError().setVisible(true);
            return false;
        } else {
            model.sendMessage(new Message(MessageType.NEWGAME, message));
            return true;
        }
    }

    /**
     * Hermann Grieder
     * <br>
     * Cleans up the overlay and tells the gameLobbyView to show the game created popup.
     */
    private void cleanUp() {
        view.getCreateGameView().getLblError().setText("");
        view.closeActiveOverlay();
        clearTextFields();
        view.getGameLobbyView().showPopUp(view.getSelectedLanguage().getLanguageTable().get("msgGameCreated"), 200);
    }

    /**
     * Hermann Grieder
     * <br>
     * Clears the gameName textField.
     */
    private void clearTextFields() {
        this.view.getCreateGameView().getTxtGameName().setText("");
    }

}

