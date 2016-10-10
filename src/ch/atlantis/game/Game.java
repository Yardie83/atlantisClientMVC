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

        Player player0 = new Player("Hermann", Color.RED);
        player0.setPlayerId(0);
        players.add(player0);

        Player player1 = new Player("Loris", Color.BLACK);
        player1.setPlayerId(1);
        players.add(player1);

        Player player2 = new Player("Heval", Color.GREEN);
        player2.setPlayerId(2);
        players.add(player2);

        Player player3 = new Player("Fabian", Color.BLUE);
        player3.setPlayerId(3);
        players.add(player3);

        gameBoardView = new GameBoardView(players, view);

        new GameController(new GameModel(), model, gameBoardView);
    }
}