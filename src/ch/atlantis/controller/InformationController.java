package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Created by LorisGrether on 20.12.2016.
 */
public class InformationController {

    final private AtlantisModel model;
    final private AtlantisView view;

    public InformationController(AtlantisModel model, AtlantisView view){
        this.model = model;
        this.view = view;

        handleLoginViewControls();
    }

    private void handleLoginViewControls() {

        view.getInformationView().getBtnClose().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.closeActiveOverlay();
            }
        });
    }
}
