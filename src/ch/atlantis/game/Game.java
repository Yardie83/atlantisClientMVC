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

        Player player0 = new Player(0);
        player0.setPlayerName("Hermann");
        players.add(player0);

        Player player1 = new Player(1);
        player1.setPlayerName("Heval");
        players.add(player1);

        Player player2 = new Player(2);
        player2.setPlayerName("Loris");
        players.add(player2);

        Player player3 = new Player(3);
        player3.setPlayerName("Fabian");
        players.add(player3);

        gameBoardView = new GameBoardView(players, view);

        new GameController(new GameModel(), model, gameBoardView);
    }
}