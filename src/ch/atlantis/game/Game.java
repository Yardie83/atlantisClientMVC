package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Game {

    private GameBoard gameBoard;
    private ArrayList<Player> players = new ArrayList<>(4);

    public Game(AtlantisModel model, AtlantisView view) {

        this.gameBoard = new GameBoard(view.heightProperty().getValue(), view.widthProperty().getValue(), players, view);

        new GameController(model, gameBoard);

        gameBoard.show();
    }
}
