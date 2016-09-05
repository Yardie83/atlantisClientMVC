package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Language;
import ch.atlantis.view.AtlantisView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;

import java.lang.reflect.Method;

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

                System.out.println("do bini");

                String culture = view.getOptionsView().getSelectedComboBoxLanguage();
                System.out.println(culture);

                if (culture != model.getSelectedLanguage()) {

                    if (changeLanguage(culture)) {
                        model.setSelectedLanguage(culture);
                        view.getOptionsStage().close();
                    } else {
                        //TODO: Error message
                    }
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

    private boolean changeLanguage(String culture) {

        System.out.println("change language");

        Language language = getSelectetLanguage(culture);

        if (language != null) {

            for (Control control : view.getGameLobbyView().getGameLobbyControls()) {

                if (control instanceof Button) {

                    Button button = (Button) control;

                    for (String id : language.getLanguageTable().values()) {

                        System.out.println(id);

                        button.setText("hallo");

                        if (button.getId() != null) {

                            //if (button.getId().toString().equals(value)) {}

                            System.out.println(button.getId().toString());
                        }
                    }
                }
            }
        } else {

            return false;
        }

        return true;
    }

    private Language getSelectetLanguage(String culture) {

        for (Language language : model.getLanguageList()) {

            if (language.getCulture() == culture) ;

            return language;
        }

        return null;

    }
    //END Handle Options Controls
}
