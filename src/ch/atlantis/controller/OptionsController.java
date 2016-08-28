package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Created by Hermann Grieder on 28.08.2016.
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
}
