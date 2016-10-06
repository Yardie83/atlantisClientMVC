package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Game {

    private GameBoardView gameBoardView;
    private ArrayList<Player> players;

    public Game(AtlantisModel model, AtlantisView view) {


        players = new ArrayList<>(4);

        players.add(new Player("Hermann", Color.RED));
        players.add(new Player("Loris", Color.BLACK));
        players.add(new Player("Heval", Color.GREEN));
        players.add(new Player("Fabian", Color.BLUE));

        gameBoardView = new GameBoardView(players, view);

        new GameController(new GameModel(), model, gameBoardView);
    }
}