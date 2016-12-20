package ch.atlantis.view;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Language;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Hermann Grieder on 17.07.2016.
 * <p>
 * This class acts as a hub from where the different views,
 * like the GameLobby, the Options etc. get instantiated.
 */

public class AtlantisView {

    private IntroView introView;
    private Stage introStage;
    private AtlantisModel model;

    private GameLobbyView gameLobbyView;

    private CreateGameView createGameView;
    private Stage createGameStage;

    private LoginView loginView;
    private Stage loginStage;

    private InformationView informationView;
    private Stage informationStage;

    private NewProfileView newProfileView;
    private Stage profileStage;

    private OptionsView optionsView;
    private Stage optionsStage;

    private SimpleIntegerProperty height;
    private SimpleIntegerProperty width;

    private ArrayList<Control> controls;

    private boolean fullscreen;
    private Stage activeOverlayStage;

    private Language selectedLanguage;

    public AtlantisView(Stage introStage, AtlantisModel model) {

        this.introStage = introStage;
        this.model = model;
        width = new SimpleIntegerProperty(1280);
        height = new SimpleIntegerProperty(800);

        this.setSelectedLanguage(model.getSelectedLanguage(model.getConfigLanguage()));

        this.setLabelLanguageText();

        controls = new ArrayList<>();
    }

    private void setLabelLanguageText() {
    }

    /**
     * Creates the Intro view
     * <p>
     * Hermann Grieder
     */

    public void createIntroView() {
        this.introView = new IntroView(introStage);
    }

    /**
     * Creates the GameLobby view
     * <p>
     * Hermann Grieder
     *
     * @param fullscreen Shows the GameLobby in fullscreen if true
     */

    public void createGameLobbyView(Boolean fullscreen) {

        this.fullscreen = fullscreen;

        if (gameLobbyView == null) {

            if (fullscreen) {
                width.setValue(Screen.getPrimary().getBounds().getWidth());
                height.setValue(Screen.getPrimary().getBounds().getHeight());
                gameLobbyView = new GameLobbyView(height.getValue(), width.getValue(), true);
                //ScenicView.show(gameLobbyView);
            } else {
                gameLobbyView = new GameLobbyView(height.getValue(), width.getValue(), false);
                bindSizeToStage();
            }
        }

        getControls(this.gameLobbyView);
        setControlText(controls);
    }

    public void updateGameList() {

        // Only if the list is not empty go on
        if (model.getGameList().size() != 0) {
            // Clear the current list in the gameLobby every time we receive the list
            gameLobbyView.getGameListView().getItems().clear();

            // Reverse the new list, so the newest games are on top
            Collections.reverse(model.getGameList());

            // Add each game in the gameList to the ListView
            for (String s : model.getGameList()) {
                String[] gameInfo = s.split(",");
                if (!gameInfo[0].equalsIgnoreCase(" ")) {

                    String gameName = gameInfo[0];
                    int currentJoinedUsers = Integer.parseInt(gameInfo[2]);
                    String outOf = selectedLanguage.getLanguageTable().get("msgOf");
                    Integer numberOfPlayers = Integer.parseInt(gameInfo[1]);
                    String players = selectedLanguage.getLanguageTable().get("msgPlayers");

                    gameLobbyView.getGameListView().getItems().add(
                            gameName
                                    + " : "
                                    + currentJoinedUsers + " "
                                    + outOf
                                    + " " + numberOfPlayers + " " + players);
                }
            }
            // Clear the list in the model to avoid duplicate entries the next time we receive the list
            model.getGameList().clear();
        }
    }

    /**
     * Binds the width and the height to the GameLobby Stage to ensure that
     * the overlays have the same dimensions as the game lobby stage when the
     * user adjust the windows size. Otherwise the overlays have the dimensions
     * of the first instantiation of the GameLobby and would either be to small
     * or to big.
     * <p>
     * Hermann Grieder
     */

    //Workaround: There seems to be a padding or margin of sorts on the stage,
    //that's why we subtract a couple pixels of the width and the height.
    public void bindSizeToStage() {
        width.bind(gameLobbyView.getGameLobbyStage().widthProperty());
        height.bind(gameLobbyView.getGameLobbyStage().heightProperty());
    }

    /**
     * Creates the Create Game view
     * <p>
     * Hermann Grieder
     */

    public void createCreateGameView() {
        Stage parentStage = gameLobbyView.getGameLobbyStage();
        if (this.createGameView == null) {
            this.createGameView = new CreateGameView(height.getValue(), width.getValue());
            createGameStage = new Stage();
            createGameStage.setScene(new Scene(createGameView));
            setupOverlay(createGameStage, parentStage, "css_CreateGameView");
        }
        setXYLocation(createGameStage, parentStage);
        setDimensions(createGameStage, parentStage);

        getControls(this.createGameView);
        setControlText(this.controls);

        activeOverlayStage = createGameStage;
    }

    /**
     * Creates the Login view
     * <p>
     * Hermann Grieder
     */
    public void createLoginView() {
        Stage parentStage = gameLobbyView.getGameLobbyStage();
        if (loginView == null) {
            loginView = new LoginView(height.getValue(), width.getValue());
            loginStage = new Stage();
            loginStage.setScene(new Scene(loginView));
            setupOverlay(loginStage, parentStage, "css_LoginView");
        }
        setXYLocation(loginStage, parentStage);
        setDimensions(loginStage, parentStage);

        getControls(this.loginView);
        setControlText(this.controls);

        activeOverlayStage = loginStage;
    }

    public void createInformationView() {
        Stage parentStage = gameLobbyView.getGameLobbyStage();
        if (informationView == null) {
            informationView = new InformationView(height.getValue(), width.getValue());
            informationStage = new Stage();
            informationStage.setScene(new Scene(informationView));
            setupOverlay(informationStage, parentStage, "css_InformationView");
        }
        setXYLocation(informationStage, parentStage);
        setDimensions(informationStage, parentStage);

        getControls(this.informationView);
        setControlText(this.controls);

        activeOverlayStage = informationStage;
    }

    /**
     * Creates the New Profile view
     * <p>
     * Hermann Grieder
     */
    public void createNewProfileView() {
        Stage parentStage = gameLobbyView.getGameLobbyStage();
        if (this.newProfileView == null) {
            this.newProfileView = new NewProfileView(height.getValue(), width.getValue());
            this.profileStage = new Stage();
            this.profileStage.setScene(new Scene(this.newProfileView));
            setupOverlay(this.profileStage, parentStage, "css_NewProfileView");
        }
        setXYLocation(this.profileStage, parentStage);
        setDimensions(this.profileStage, parentStage);

        getControls(this.newProfileView);
        setControlText(this.controls);

        this.activeOverlayStage = this.profileStage;
    }

    /**
     * Creates the Options view
     * <p>
     * Hermann Grieder
     */

    public void createOptionsView() {
        ArrayList<Language> languageList = model.getLanguageList();
        String culture = model.getConfigLanguage();
        Stage parentStage = gameLobbyView.getGameLobbyStage();
        if (this.optionsView == null) {
            this.optionsView = new OptionsView(height.getValue(), width.getValue(), languageList, culture, model.getIsMusic());
            this.optionsStage = new Stage();
            this.optionsStage.setScene(new Scene(this.optionsView));
            setupOverlay(this.optionsStage, parentStage, "css_OptionsView");
        }
        setXYLocation(this.optionsStage, parentStage);
        setDimensions(this.optionsStage, parentStage);

        getControls(this.optionsView);
        setControlText(this.controls);

        this.activeOverlayStage = this.optionsStage;
    }

    public void setXYLocation(Stage overlayStage, Stage parentStage) {
        overlayStage.setX(parentStage.getX());
        overlayStage.setY(parentStage.getY());
    }

    // TODO: This does not work. The overlayStage does not react. I think it must be the root pane, but how to get
    // there?
    public void setDimensions(Stage overlayStage, Stage parentStage) {
        overlayStage.setMinHeight(parentStage.getHeight());
        overlayStage.setMinWidth(parentStage.getWidth());
    }

    /**
     * Loris Grether
     * <br>
     *
     * @param pane
     */
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

    /**
     * Loris Grether
     * <br>
     *
     * @param controls
     */
    private void setControlText(ArrayList<Control> controls) {

        for (Control control : controls) {
            if (control instanceof Button) {
                Button button = (Button) control;
                addLanguageTextToButtonControl(button);
            } else if (control instanceof RadioButton) {
                RadioButton button = (RadioButton) control;
                addLanguageTextToButtonControl(button);
            } else if (control instanceof Label) {
                Label label = (Label) control;
                addLanguageTextToLabelControl(label);
            }
        }
        controls.clear();
    }

    /**
     * Loris Grether
     * <br>
     *
     * @param button
     */
    private void addLanguageTextToButtonControl(ButtonBase button) {
        if (selectedLanguage != null) {
            for (String id : selectedLanguage.getLanguageTable().keySet()) {
                if (button.getId() != null) {
                    if (button.getId().equals(id)) {
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
                        label.setText(selectedLanguage.getLanguageTable().get(id));
                    }
                }
            }
        }
    }

    /**
     * Sets the settings for the stage to be overlaid onto the parent stage.
     * Matches the dimensions and location of the overlay to the dimensions
     * of the parent stage.
     * <p>
     * Hermann Grieder
     *
     * @param overlayStage The stage of the overlay to set up
     */

    public void setupOverlay(Stage overlayStage, Stage parentStage, String cssString) {

        if (cssString != null) {
            String css = this.getClass().getResource("../res/css/" + cssString + ".css").toExternalForm();
            overlayStage.getScene().getStylesheets().add(css);
        }
        overlayStage.getScene().getRoot().prefWidth(parentStage.getWidth());
        overlayStage.getScene().getRoot().prefHeight(parentStage.getHeight());
        // Make it so that the overlays block access to the parentStage
        overlayStage.initModality(Modality.WINDOW_MODAL);
        //Set opacity for the overlays
        overlayStage.opacityProperty().setValue(0.99);
        //Remove the Window decorations minimize, maximize and close button and the frame
        overlayStage.initStyle(StageStyle.TRANSPARENT);
        //Make it so that the overlays are always on top of the other windows
        overlayStage.setAlwaysOnTop(true);
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
        return createGameStage;
    }

    public LoginView getLoginView() {
        return this.loginView;
    }

    public Stage getLoginStage() {
        return loginStage;
    }

    public InformationView getInformationView() {return this.informationView;}

    public Stage getInformationStage() {return this.informationStage;}

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

    public SimpleIntegerProperty heightProperty() {
        return height;
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

    public void closeActiveOverlay() {
        this.activeOverlayStage.close();
    }

    public void showOptions() {
        if (this.optionsView == null) {
            createOptionsView();
        }
        this.optionsStage.show();
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public String getListInfo() {
        return gameLobbyView.getGameListView().getSelectionModel().getSelectedItem().toString();
    }


}
