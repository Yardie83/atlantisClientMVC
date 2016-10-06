package ch.atlantis.view;

import ch.atlantis.util.Language;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 */
public class AtlantisView {

    private IntroView introView;
    private Stage introStage;

    private GameLobbyView gameLobbyView;

    private CreateGameView createGameView;
    private Stage createGameViewStage;

    private LoginView loginView;
    private Stage loginStage;

    private NewProfileView newProfileView;
    private Stage profileStage;

    private OptionsView optionsView;
    private Stage optionsStage;

    private SimpleIntegerProperty height;
    private SimpleIntegerProperty width;

    private ArrayList<Control> controls;

    private boolean fullscreen;

    private Language selectedLanguage;


    public AtlantisView(Stage introStage) {

        this.introStage = introStage;
        width = new SimpleIntegerProperty(1280);
        height = new SimpleIntegerProperty(800);

        controls = new ArrayList<>();
    }


    public void createIntroView() {
        this.introView = new IntroView(introStage);
    }

    public void createGameLobbyView(Boolean fullscreen) {

        this.fullscreen = fullscreen;

        if (gameLobbyView == null) {

            if (fullscreen) {
                width.setValue(Screen.getPrimary().getBounds().getWidth());
                height.setValue(Screen.getPrimary().getBounds().getHeight());
                this.gameLobbyView = new GameLobbyView(height.getValue(), width.getValue(), true);
                //ScenicView.show(gameLobbyView);
            } else {
                this.gameLobbyView = new GameLobbyView(height.getValue(), width.getValue(), false);
                bindSizeToStage();
            }
        }

            getControls(this.gameLobbyView);
            setControlText(controls);
    }

    public void bindSizeToStage() {
        /*Bind the width and the height to the GameLobby Stage to ensure that
         * the overlays have the same dimensions as the game lobby stage
         *
         * Workaround: There seems to be a padding or margin of sorts on the stage,
         * that's why we subtract a couple pixels of the width and the height.
         */
        width.bind(gameLobbyView.getGameLobbyStage().widthProperty().subtract(8));
        height.bind(gameLobbyView.getGameLobbyStage().heightProperty().subtract(8));
    }

    public void createCreateGameView() {
        this.createGameView = new CreateGameView(height.getValue(), width.getValue());
        Scene scene = new Scene(createGameView);
        createGameViewStage = new Stage();
        setupOverlay(createGameViewStage, scene, "CreateGameView");
    }

    public void createLoginView() {
        if (this.loginView == null) {
            this.loginView = new LoginView(height.getValue(), width.getValue());
            Scene scene = new Scene(loginView);
        }
            loginStage = new Stage();
            setupOverlay(loginStage, loginView.getScene(), "LoginView");
    }

    public void createNewProfileView() {

        if (this.newProfileView == null){
        this.newProfileView = new NewProfileView(height.getValue(), width.getValue());
            Scene scene = new Scene(newProfileView);
        }
        getControls(this.newProfileView);
        setControlText(controls);
        profileStage = new Stage();
        setupOverlay(profileStage, newProfileView.getScene(), "NewProfileView");
    }

    public void createOptionsView(ArrayList<Language> languageList, String culture) {
        this.optionsView = new OptionsView(height.getValue(), width.getValue(), languageList, culture);
        Scene scene = new Scene(optionsView);
        optionsStage = new Stage();
        setupOverlay(optionsStage, scene, "OptionsView");
    }

    private void getControls(Pane pane) {

        for (Node node : pane.getChildren()) {
            if (node instanceof Pane) {
                getControls((Pane) node);
            } else if (node instanceof Control) {
                Control c = (Control) node;
                controls.add(c);
            }
        }
    }

    private void setControlText(ArrayList<Control> controls){

        for (Control control : controls) {

            if (control instanceof Button) {

                Button button = (Button) control;
                addLanguageTextToButtonControl(button);
            }

            if (control instanceof Label) {

                Label label = (Label) control;
                addLanguageTextToLabelControl(label);
            }
        }

        controls.clear();
    }

    private void addLanguageTextToButtonControl(Button button) {

        if (selectedLanguage != null) {

            for (String id : selectedLanguage.getLanguageTable().keySet()) {

                if (button.getId() != null) {

                    if (button.getId().equals(id)) {

                        //System.out.println("!!! LADIES AND GENTLEMEN WE HAVE A MATCH !!!");

                        //System.out.println("LE TEXT: " + language.getLanguageTable().get(id));

                        button.setText(selectedLanguage.getLanguageTable().get(id));
                    }
                }
            }
        }
    }

    private void addLanguageTextToLabelControl(Label label) {

        if (selectedLanguage != null) {

            for (String id : selectedLanguage.getLanguageTable().keySet()) {

                if (label.getId() != null) {

                    if (label.getId().equals(id)) {

                        //System.out.println("!!! LADIES AND GENTLEMEN WE HAVE A MATCH !!!");

                        //System.out.println("LE TEXT: " + language.getLanguageTable().get(id));

                        label.setText(selectedLanguage.getLanguageTable().get(id));
                    }
                }
            }
        }
    }

    private void setupOverlay(Stage stage, Scene scene, String cssString) {
        //Get the css files and add them to the scene
        String css = this.getClass().getResource("../res/css/css_" + cssString + ".css").toExternalForm();
        scene.getStylesheets().add(css);
        // Make it so that the overlays block the GameLobby
        stage.initModality(Modality.APPLICATION_MODAL);
        //Match the X and Y to the Game Lobby's X and Y coordinates
        stage.setX(gameLobbyView.getGameLobbyStage().getX());
        stage.setY(gameLobbyView.getGameLobbyStage().getY());
        //Set the dimensions of the Stage
        stage.setMinHeight(height.getValue());
        stage.setMinWidth(width.getValue());
        //Set opacity for the overlays
        stage.opacityProperty().setValue(0.95);
        //Remove the Window decorations minimize, maximize and close button and the frame
        stage.initStyle(StageStyle.TRANSPARENT);
        //Make it so that the overlays are always on top of the other windows
        stage.setAlwaysOnTop(true);
        //Set the scene and show it
        stage.setScene(scene);
        stage.show();
    }


    public IntroView getIntroView() {
        return introView;
    }

    public Stage getIntroStage() {
        return introStage;
    }

    public GameLobbyView getGameLobbyView() {
        return gameLobbyView;
    }

    public CreateGameView getCreateGameView() {
        return createGameView;
    }

    public Stage getCreateGameStage() {
        return createGameViewStage;
    }

    public LoginView getLoginView() {
        return this.loginView;
    }

    public Stage getLoginStage() {
        return loginStage;
    }

    public NewProfileView getNewProfileView() {
        return newProfileView;
    }

    public Stage getProfileStage() {
        return profileStage;
    }

    public OptionsView getOptionsView() {
        return optionsView;
    }

    public Stage getOptionsStage() {
        return optionsStage;
    }

    public int getHeight() {
        return height.get();
    }

    public SimpleIntegerProperty heightProperty() {
        return height;
    }

    public void setHeight(int height) {
        this.height.set(height);
    }

    public int getWidth() {
        return width.get();
    }

    public SimpleIntegerProperty widthProperty() {
        return width;
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public boolean setSelectedLanguage(Language selectedLanguage) {

        if (selectedLanguage != null) {

            this.selectedLanguage = selectedLanguage;
            return true;
        }
        return false;
    }
}