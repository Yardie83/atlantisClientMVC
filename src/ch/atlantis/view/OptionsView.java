package ch.atlantis.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Hermann Grieder on 21.07.2016.
 */
public class OptionsView extends Pane {

    private final VBox root;

    //TOP element
    private Label lblOptions;

    // CENTER elements
    private GridPane centerPane;

    private Label lblFullScreen;
    private Label lblSoundToggle;
    private Label lblLanguage;

    private RadioButton radioBtnFullScreenOn;
    private RadioButton radioBtnFullScreenOff;

    private RadioButton radioBtnSoundOn;
    private RadioButton radioBtnSoundOff;

    private RadioButton radioBtnEnglish;
    private RadioButton radioBtnGerman;


    //BOTTOM elements
    private HBox bottomPane;
    private Button btnApply;
    private Button btnCancel;


    public OptionsView(int height, int width) {

        root = new VBox(30);
        root.setMinHeight(height);
        root.setMinWidth(width);

        root.getChildren().add(createTop());
        root.getChildren().add(createContent());
        root.getChildren().add(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }


    private Label createTop() {

        lblOptions = new Label("OPTIONS");
        lblOptions.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));

        return lblOptions;

    }

    private GridPane createContent() {

        centerPane = new GridPane();

        lblFullScreen = new Label("Fullscreen");
        ToggleGroup radioBtnGroupFullscreen = new ToggleGroup();
        radioBtnFullScreenOn = new RadioButton("On");
        radioBtnFullScreenOn.setSelected(true);
        radioBtnFullScreenOff = new RadioButton("Off");
        radioBtnGroupFullscreen.getToggles().addAll(radioBtnFullScreenOn, radioBtnFullScreenOff);

        lblSoundToggle = new Label("Sound");
        ToggleGroup radioBtnGroupSound = new ToggleGroup();
        radioBtnSoundOn = new RadioButton("On");
        radioBtnSoundOn.setSelected(true);
        radioBtnSoundOff = new RadioButton("Off");
        radioBtnGroupSound.getToggles().addAll(radioBtnSoundOn, radioBtnSoundOff);

        lblLanguage = new Label("ch.atlantis.util.Language");
        ToggleGroup radioBtnGroupLanguage = new ToggleGroup();
        radioBtnEnglish = new RadioButton("English");
        radioBtnEnglish.setSelected(true);
        radioBtnGerman = new RadioButton("German");
        radioBtnGroupLanguage.getToggles().addAll(radioBtnEnglish, radioBtnGerman);

        centerPane.add(lblFullScreen, 0, 0);
        centerPane.add(radioBtnFullScreenOn, 1, 0);
        centerPane.add(radioBtnFullScreenOff, 2, 0);

        centerPane.add(lblSoundToggle, 0, 2);
        centerPane.add(radioBtnSoundOn, 1, 2);
        centerPane.add(radioBtnSoundOff, 2, 2);

        centerPane.add(lblLanguage, 0, 1);
        centerPane.add(radioBtnEnglish, 1, 1);
        centerPane.add(radioBtnGerman, 2, 1);

        return centerPane;
    }

    private HBox createBottom() {

        bottomPane = new HBox(30);

        btnApply = new Button("Apply");
        btnCancel = new Button("Cancel");

        bottomPane.getChildren().addAll(btnApply, btnCancel);

        return bottomPane;
    }

    private void defineStyleClass() {

        /* Common Style Class for the buttons in the Options View*/
        btnApply.getStyleClass().add("buttons");
        btnCancel.getStyleClass().add("buttons");

        /*Common Style Class for the Labels and TextFields in the CENTER*/
        lblFullScreen.getStyleClass().add("labels");
        lblSoundToggle.getStyleClass().add("labels");
        lblLanguage.getStyleClass().add("labels");

        /*Style ID for the root BorderPane */
        root.setId("root");

        /*Style IDs for the controls in the Options View*/
        lblOptions.setId("lblOptions");
        lblFullScreen.setId("lblFullScreen");
        lblSoundToggle.setId("lblSoundToggle");
        lblLanguage.setId("lblLanguage");

        radioBtnFullScreenOn.getStyleClass().add("radioButtons");
        radioBtnFullScreenOff.getStyleClass().add("radioButtons");

        radioBtnSoundOn.getStyleClass().add("radioButtons");
        radioBtnSoundOff.getStyleClass().add("radioButtons");

        radioBtnEnglish.getStyleClass().add("radioButtons");
        radioBtnGerman.getStyleClass().add("radioButtons");


        btnApply.setId("btnApply");
        btnCancel.setId("btnCancel");

        centerPane.setId("centerPane");
        bottomPane.setId("bottomPane");
    }

    public RadioButton getRadioBtnEnglish() {
        return radioBtnEnglish;
    }

    public RadioButton getRadioBtnGerman() {
        return radioBtnGerman;
    }

    public Button getBtnApply() {
        return btnApply;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }
}
