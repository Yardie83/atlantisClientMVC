import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class AtlantisView {

    private Stage primaryStage;
    private AtlantisModel model;
    private IntroView introView;
    private GameLobbyView gameLobbyView;

    private Scene scene;

    public AtlantisView(Stage primaryStage, AtlantisModel model) {

        this.model = model;
        this.primaryStage = primaryStage;

        initStage();
    }

    private void initStage() {
        primaryStage.setTitle("Atlanits");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
    }

    public void createIntroView(){

        this.introView = new IntroView();
        Scene scene = new Scene(introView);
        setScene(scene);
    }

    public void createGameLobbyView(){

        this.gameLobbyView = new GameLobbyView(model);
        Scene scene = new Scene(gameLobbyView);
        setScene(scene);
    }

    private void setScene(Scene scene){
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

    public Scene getScene(){
        return scene;
    }

    public IntroView getIntroView() {
        return introView;
    }

    public GameLobbyView getGameLobbyView() {
        return gameLobbyView;
    }
}