import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 */
public class AtlantisView {

    private Stage primaryStage;
    private Scene scene;

    private AtlantisModel model;

    private IntroView introView;
    private GameLobbyView gameLobbyView;
    private CreateGameView createGameView;
    private OptionsView optionsView;
    private LoginView loginView;

    private NewProfileView newProfileView;

    private Stage createGameViewStage;
    private Stage loginStage;
    private Stage profileStage;
    private Stage optionsStage;

    public AtlantisView(Stage primaryStage, AtlantisModel model) {

        this.model = model;
        this.primaryStage = primaryStage;
        initStage();
    }

    private void initStage() {
        primaryStage.setTitle("Atlantis");
        primaryStage.setFullScreenExitHint("");
        //primaryStage.setMaximized(true);
        primaryStage.setResizable(true);

        //Start the application in the center of the screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        primaryStage.setX(toolkit.getScreenSize().getHeight()/4);
        primaryStage.setY(toolkit.getScreenSize().getWidth()/8);
    }

    public void toggleFullscreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    //TODO: The methods createIntroView, createGameLobbyView, createCreateGameView, createLoginView, createNewProfileView and createOptionsView are all setup the same way. It should be possible to create one method to handle all.
    public void createIntroView() {
        toggleFullscreen();
        this.introView = new IntroView();
        Scene scene = new Scene(introView);
        setScene(scene);
    }

    public void createGameLobbyView() {
        toggleFullscreen();
        this.gameLobbyView = new GameLobbyView(this);
        String css = this.getClass().getResource("/res/css_GameLobby.css").toExternalForm();
        Scene scene = new Scene(gameLobbyView);
        scene.getStylesheets().add(css);
        setScene(scene);
    }


    public void createCreateGameView() {
        this.createGameView = new CreateGameView(this);
        String css = this.getClass().getResource("/res/css_CreateGameView.css").toExternalForm();
        Scene scene = new Scene(createGameView);
        scene.getStylesheets().add(css);
        createGameViewStage = new Stage();
        setupOverlay(createGameViewStage, scene);
    }

    public void createLoginView() {
        this.loginView = new LoginView(this);
        String css = this.getClass().getResource("/res/css_LoginView.css").toExternalForm();
        Scene scene = new Scene(loginView);
        scene.getStylesheets().add(css);
        loginStage = new Stage();
        setupOverlay(loginStage, scene);
    }

    public void createNewProfileView() {

        this.newProfileView = new NewProfileView(this);
        String css = this.getClass().getResource("/res/css_NewProfileView.css").toExternalForm();
        Scene scene = new Scene(newProfileView);
        scene.getStylesheets().add(css);
        profileStage = new Stage();
        setupOverlay(profileStage, scene);
    }

    public void createOptionsView() {
        this.optionsView = new OptionsView(this);
        String css = this.getClass().getResource("/res/css_OptionsView.css").toExternalForm();
        Scene scene = new Scene(optionsView);
        scene.getStylesheets().add(css);
        optionsStage = new Stage();
        setupOverlay(optionsStage, scene);
    }

    private void setupOverlay(Stage stage, Scene scene){
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setX(primaryStage.getX());
        stage.setY(primaryStage.getY());
        stage.setHeight(primaryStage.getHeight());
        stage.setWidth(primaryStage.getWidth());
        stage.opacityProperty().setValue(0.9);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }

    private void setScene(Scene scene) {
        this.scene = scene;
        primaryStage.setScene(scene);
    }

    public void start() {
        primaryStage.show();
    }

    public void stop() {
        primaryStage.hide();
    }

    public Stage getStage() {
        return primaryStage;
    }

    public Scene getScene() {
        return scene;
    }

    public IntroView getIntroView() {
        return introView;
    }

    public GameLobbyView getGameLobbyView() {
        return gameLobbyView;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
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
}
