package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Message;
import ch.atlantis.view.AtlantisView;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Game {

    private final GameController gameController;
    private Player localPlayer;

    public Game(AtlantisModel model, AtlantisView view, Message message, Player localPlayer) {
        this.localPlayer = localPlayer;
        GameModel gameModel = new GameModel(message, localPlayer);
        GameBoardView gameBoardView = new GameBoardView(gameModel, view);
        gameController = new GameController(view, model, gameModel, gameBoardView);
    }

    public void showGame() {
        gameController.showGame();
    }
}