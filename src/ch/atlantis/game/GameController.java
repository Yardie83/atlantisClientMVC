package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */
public class GameController {


    private GameBoardView gameBoardView;

    private GameModel gameModel;
    private AtlantisModel model;

    public GameController(GameModel gameModel, AtlantisModel model, GameBoardView gameBoardView) {
        this.gameModel = gameModel;
        this.model = model;
        this.gameBoardView = gameBoardView;
        gameBoardView.show();

        handleUserInput();
    }

    private void handleUserInput() {
        for (Card c : gameBoardView.getPathArray()){
            c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println(c.getpathId());
                }
            });
        }
    }
}
