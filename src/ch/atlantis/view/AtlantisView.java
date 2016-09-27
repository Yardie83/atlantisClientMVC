package ch.atlantis.view;

import ch.atlantis.util.Language;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 17.07.2016.
 * <p>
 * This class acts as a hub from where the different views,
 * like the GameLobby, the Options etc. get instantiated.
 */

public class AtlantisView {

    private IntroView introView;
    private Stage introStage;

    private GameLobbyView gameLobbyView;

    private CreateGameView createGameView;
    private Stage createGameStage;

    private LoginView loginView;
    private Stage loginStage;

    private NewProfileView newProfileView;
    private Stage profileStage;

    private OptionsView optionsView;
    private Stage optionsStage;

    private SimpleIntegerProperty height;
    private SimpleIntegerProperty width;

    private boolean fullscreen;
    private Stage activeOverlayStage;


    public AtlantisView(Stage introStage) {

        this.introStage = introStage;
        width = new SimpleIntegerProperty(1280);
        height = new SimpleIntegerProperty(800);
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
        width.bind(gameLobbyView.getGameLobbyStage().widthProperty().subtract(8));
        height.bind(gameLobbyView.getGameLobbyStage().heightProperty().subtract(8));
    }

    /**
     * Creates the Create Game view
     * <p>
     * Hermann Grieder
     */

    public void createCreateGameView(Stage parentStage) {
        this.createGameView = new CreateGameView(height.getValue(), width.getValue());
        Scene scene = initScene(createGameView, "css_CreateGameView");
        createGameStage = new Stage();
        setupOverlay(createGameStage, parentStage);
        createGameStage.setScene(scene);

        activeOverlayStage = createGameStage;

    }

    /**
     * Creates the Login view
     * <p>
     * Hermann Grieder
     */

    public void createLoginView(Stage parentStage) {
        this.loginView = new LoginView(height.getValue(), width.getValue());
        Scene scene = initScene(loginView, "css_LoginView");
        loginStage = new Stage();
        setupOverlay(loginStage, parentStage);
        loginStage.setScene(scene);

        activeOverlayStage = loginStage;
    }

    /**
     * Creates the New Profile view
     * <p>
     * Hermann Grieder
     */

    public void createNewProfileView(Stage parentStage) {
        this.newProfileView = new NewProfileView(height.getValue(), width.getValue());
        Scene scene = initScene(newProfileView, "css_NewProfileView");
        profileStage = new Stage();
        setupOverlay(profileStage, parentStage);
        profileStage.setScene(scene);

        activeOverlayStage = profileStage;
    }

    /**
     * Creates the Options view
     * <p>
     * Hermann Grieder
     */

    public void createOptionsView(ArrayList<Language> languageList, Stage parentStage) {
        this.optionsView = new OptionsView(height.getValue(), width.getValue(), languageList);
        Scene scene = initScene(optionsView, "css_OptionsView");
        optionsStage = new Stage();
        setupOverlay(optionsStage, parentStage);
        optionsStage.setScene(scene);

        activeOverlayStage = optionsStage;
    }

    /**
     * Creates a new scene, adds the CSS file and returns that scene.
     * The css file has to be placed in the /res/css/ folder
     * <p>
     * Hermann Grieder
     *
     * @param root      The root node of the scene graph
     * @param cssString Nullable. Name of the CSS file excluding ".css"
     * @return Scene
     */

    private Scene initScene(Parent root, String cssString) {
        Scene scene = new Scene(root);

        if (cssString != null) {
            String css = this.getClass().getResource("../res/css/" + cssString + ".css").toExternalForm();
            scene.getStylesheets().add(css);
        }
        return scene;
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

    private void setupOverlay(Stage overlayStage, Stage parentStage) {
        // Make it so that the overlays block access to the parentStage
        overlayStage.initModality(Modality.WINDOW_MODAL);
        //Match the X and Y to the Game Lobby's X and Y coordinates
        overlayStage.setX(parentStage.getX());
        overlayStage.setY(parentStage.getY());
        //Set the dimensions of the Stage
        overlayStage.setMinHeight(parentStage.getHeight());
        overlayStage.setMinWidth(parentStage.getWidth());
        //Set opacity for the overlays
        overlayStage.opacityProperty().setValue(0.95);
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

    public void closeActiveOverlay() {
        this.activeOverlayStage.close();
    }
}
