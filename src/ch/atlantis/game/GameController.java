package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */
public class GameController {


    private GameBoard gameBoard;

    private AtlantisModel model;

    public GameController(AtlantisModel model, GameBoard gameBoard) {

            this.model = model;
            this.gameBoard = gameBoard;
    }
}
