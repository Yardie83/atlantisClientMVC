package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Game {

    private GameBoardView gameBoardView;
    private ArrayList<Player> players;
    private String gameName;
    private int noOfPlayers;
    private int joinedPlayers;

    public Game( AtlantisModel model, AtlantisView view ) {


        players = new ArrayList<>( 4 );

        Player player0 = new Player( 0, gameName );
        player0.setPlayerName( "Hermann" );
        players.add( player0 );

        Player player1 = new Player( 1, gameName );
        player1.setPlayerName( "Heval" );
        players.add( player1 );

        Player player2 = new Player( 2, gameName );
        player2.setPlayerName( "Loris" );
        players.add( player2 );

        Player player3 = new Player( 3, gameName );
        player3.setPlayerName( "Fabian" );
        players.add( player3 );

        gameBoardView = new GameBoardView( players, view );

        new GameController( view, model, new GameModel(), gameBoardView );
    }
}