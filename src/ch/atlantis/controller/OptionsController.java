package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;

/**
 * Created by Loris Grether on 28.08.2016.
 * <p>
 * Controller for the OptionsView
 * Handles all events performed on the controls in the OptionsView
 * Blablabla
 */

public class OptionsController {

    final private AtlantisModel model;
    final private AtlantisView view;

    public OptionsController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;
        handleOptionsControls();
    }

    // Handle Options Controls' Action Events in the Options View
    private void handleOptionsControls() {

        view.getOptionsView().getBtnApply().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //Fullscreen Settings
                //fullscreenSettings();

                //Music Settings
                musicSettings();

                //Language Settings
                languageSettings();

                view.getOptionsStage().close();
            }
        });

        view.getOptionsView().getBtnCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.getOptionsStage().close();
            }
        });
    }

    /*
    * The method musicSettings turns the program music on and off
    *
    * Loris Grether
    * */
    private void musicSettings() {

        RadioButton rb = (RadioButton) view.getOptionsView().getRadioBtnGroupSound().getSelectedToggle();

        if (rb.getText().equals("On") && !model.getIsMusic()) {
            model.soundController(true);
        } else if (rb.getText().equals("Off") && model.getIsMusic()) {
            model.soundController(false);
        }
    }


    /*
    *The method languageSettings changes the langage from the program when the user changes the language in the settings
    *
    * Loris Grether
    * */
    private void languageSettings() {

        String culture = view.getOptionsView().getSelectedComboBoxLanguage();

        if (culture != model.getConfigLanguage()) {

            if (view.setSelectedLanguage(model.getSelectedLanguage(culture))) {

                view.createGameLobbyView(false);

                model.setConfigLanguage(culture);
                view.getOptionsView().setSelectedComboboxLanguage(culture);
                //view.getOptionsStage().close();
            } else {
                //TODO: (Loris) Error message
            }
        } else {
            //view.getOptionsStage().close();
        }
    }
    //END Handle Options Controls
}

