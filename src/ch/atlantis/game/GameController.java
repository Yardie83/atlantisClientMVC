package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;

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
    }
}
