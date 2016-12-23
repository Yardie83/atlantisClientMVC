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
 *
 * The intro view shows the intro video on start of the application.
 */
public class IntroView extends Pane {

    private MediaPlayer mp;

    public IntroView(Stage introStage) {

        Logger logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        Scene introScene = new Scene(this);
        introStage.setScene(introScene);
        introStage.setFullScreen(true);
        introStage.setFullScreenExitHint("");

        try {
            Media media = new Media(Paths.get("src/ch/atlantis/res/atlantis.mp4").toUri().toString());
            mp = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mp);
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

