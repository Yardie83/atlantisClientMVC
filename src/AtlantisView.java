import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private OptionsView optionsView;
    private LoginView loginView;
    private Stage loginStage;

    private NewProfileView newProfileView;

    private Stage profileStage;

    public AtlantisView(Stage primaryStage, AtlantisModel model) {

        this.model = model;
        this.primaryStage = primaryStage;
        initStage();
    }

    private void initStage() {
        primaryStage.setTitle("Atlantis");
        primaryStage.setFullScreenExitHint("");
        //primaryStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        //primaryStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        //primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.setX(0);
        primaryStage.setY(0);

    }

    public void toggleFullscreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    public void createIntroView() {
        toggleFullscreen();
        this.introView = new IntroView();
        Scene scene = new Scene(introView);
        setScene(scene);
    }

    public void createGameLobbyView() {
        toggleFullscreen();
        this.gameLobbyView = new GameLobbyView(this);
        Scene scene = new Scene(gameLobbyView);
        String css = this.getClass().getResource("/res/css_gameLobby.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);
        setScene(scene);
    }

    public void createLoginView(){
        this.loginView = new LoginView(this);
        loginStage = new Stage();
        Scene scene = new Scene(loginView);
        String css = this.getClass().getResource("/res/css_LoginView.css").toExternalForm();
        scene.getStylesheets().add(css);
        loginStage.setScene(scene);
        loginStage.setTitle("Atlantis - Login");
        loginStage.show();
        //TODO: Ask bradley showAndWait()
    }

    public void createNewProfileView(){

        this.newProfileView = new NewProfileView(this);
        profileStage = new Stage();
        Scene scene = new Scene(newProfileView);
        String css = this.getClass().getResource("/res/css_NewProfileView.css").toExternalForm();
        scene.getStylesheets().add(css);
        profileStage.setScene(scene);
        profileStage.setTitle("Atlantis - New Profile");
        profileStage.show();
    }

    //TODO: Finish creating the options view
    public void createOptionsView() {
        this.optionsView = new OptionsView();
        Scene scene = new Scene(optionsView);
        String css = this.getClass().getResource("/res/css_OptionsView.css").toExternalForm();
        scene.getStylesheets().add(css);
        setScene(scene);
    }

    private void setScene(Scene scene) {
        this.scene = scene;
        primaryStage.setScene(scene);
    }

    public void start() {
        primaryStage.show();
    }

    public void stop() {primaryStage.hide();}

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

    public LoginView getLoginView(){
        return this.loginView;
    }

    public Stage getLoginStage() {return loginStage;}

    public NewProfileView getNewProfileView() {
        return newProfileView;
    }

    public Stage getProfileStage() {
        return profileStage;
    }

}