package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.Message;
import ch.atlantis.view.AtlantisView;

/**
 * Created by Fabian, Loris Grether and Hermann Grieder on 15/08/16.
 *
 * The game class.
 */
public class Game {

    private final GameController gameController;

    public Game(AtlantisModel model, AtlantisView view) {
        GameModel gameModel = new GameModel(model.getMessage(), model.getLocalPlayer());
        GameBoardView gameBoardView = new GameBoardView(gameModel, view);
        gameController = new GameController(view, model, gameModel, gameBoardView);

        // Loris Grether
        view.getControls(gameBoardView);
        view.setControlText(view.getListOfControls());

    }

    public void showGame() {
        gameController.startGame();
    }

    public GameController getGameController() {
        return gameController;
    }
}