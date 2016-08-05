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
        toggleFullscreen();
        this.loginView = new LoginView(this);
        Scene scene = new Scene(loginView);
        setScene(scene);
    }

    //TODO Finish creating the options view
    public void createOptionsView() {
        this.optionsView = new OptionsView();
        Scene scene = new Scene(optionsView);
        setScene(scene);
    }

    private void setScene(Scene scene) {
        this.scene = scene;
        primaryStage.setScene(scene);
    }

    public void start() {
        primaryStage.show();
    }

    public void stop() {primaryStage.hide();
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

    public LoginView getLoginView(){
        return this.loginView;
    }
}