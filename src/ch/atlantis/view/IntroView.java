package ch.atlantis.view;

import ch.atlantis.AtlantisClient;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 */
public class IntroView extends Pane {

    private Scene introScene;
    private MediaPlayer mp;
    private MediaView mediaView;
    private Logger logger;

    public IntroView(Stage introStage) {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        introScene = new Scene(this);
        introStage.setScene(introScene);
        introStage.setFullScreen(true);
        introStage.setFullScreenExitHint("");

        try {
            Media media = new Media(Paths.get("src/ch/atlantis/res/atlantis.mp4").toUri().toString());
            mp = new MediaPlayer(media);
            mediaView = new MediaView(mp);
            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();

            width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            mediaView.setPreserveRatio(false);

            this.getChildren().add(mediaView);

        } catch (Exception e) {
            logger.info("Could not load intro movie.");
            e.printStackTrace();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mp;
    }

}

