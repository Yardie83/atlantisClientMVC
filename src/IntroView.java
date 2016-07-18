import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Parent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

import java.nio.file.Paths;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 *
 */
public class IntroView extends Parent {
    AtlantisView view;

    private MediaPlayer mp;
    private MediaView mediaView;

    public IntroView(AtlantisView view) {
        this.view = view;

        try {
            Media media = new Media(Paths.get("src/res/atlantis.mp4").toUri().toString());
            mp = new MediaPlayer(media);
            mediaView = new MediaView(mp);
            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();

            width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            mediaView.setPreserveRatio(true);

            this.getChildren().add(mediaView);

        } catch (Exception e) {
            System.out.println("Could not load intro movie");
            e.printStackTrace();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mp;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public void setMediaView(MediaView mediaView) {
        this.mediaView = mediaView;
    }
}
