package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Created by Hermann Grieder on 28.08.2016.
 * <p>
 * Options View Controller
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

                String culture = view.getOptionsView().getSelectedComboBoxLanguage();
                //System.out.println(culture);

                if (culture != model.getCurrentLanguage()) {

                    if (view.setSelectedLanguage(model.getSelectedLanguage(culture))) {

                        view.createGameLobbyView(false);

                        model.setCurrentLanguage(culture);
                        view.getOptionsView().setSelectedComboboxLanguage(culture);
                        view.getOptionsStage().close();
                    } else {
                        //TODO: (Loris) Error message
                    }
                } else {
                    view.getOptionsStage().close();
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
}

