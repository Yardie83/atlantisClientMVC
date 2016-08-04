import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 *
 */
public class AtlantisView {

    private Stage primaryStage;
    private AtlantisModel model;
    private IntroView introView;
    private GameLobbyView gameLobbyView;

    private Scene scene;
    private OptionsView optionsView;

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

}