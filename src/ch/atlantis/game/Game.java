package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Game {

    private GameBoardView gameBoardView;
    private ArrayList<Player> players = new ArrayList<>(4);

    public Game(AtlantisModel model, AtlantisView view) {

        gameBoardView = new GameBoardView(players, view);

        new GameController(new GameModel(), model, gameBoardView);

        gameBoardView.show();
    }
}