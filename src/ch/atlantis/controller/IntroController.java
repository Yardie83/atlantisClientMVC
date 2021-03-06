package ch.atlantis.controller;

import ch.atlantis.AtlantisClient;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.util.logging.Logger;

/**
 * Created by Hermann Grieder on 28.08.2016.
 * <p>
 * Plays the introVideo on the start of the application and switches to the GameLobby
 * on mouseClick on the video
 */

public class IntroController {

    final private AtlantisModel model;
    final private AtlantisView view;

    private Logger logger;

    public IntroController(AtlantisModel model, AtlantisView view) {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        this.model = model;
        this.view = view;
        handleIntroViewControls();
    }

    private void handleIntroViewControls() {

        view.getIntroStage().setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    view.getIntroView().getMediaPlayer().play();
                } catch (Exception e) {
                    logger.warning("Not able to play intro video");
                }
            }
        });

        view.getIntroStage().getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.getIntroView().getMediaPlayer().stop();
                view.getIntroView().getMediaPlayer().dispose();
                view.getIntroStage().close();
                view.createGameLobbyView(true);
                new GameLobbyController(model, view);
                view.getGameLobbyView().show();
            }
        });
    }
}

