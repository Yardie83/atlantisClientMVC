package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Game {

    private ArrayList<Player> players;
    private String userName;

    public Game(AtlantisModel model, String userName) {
        this.userName = userName;
        players = new ArrayList<>();
    }

}
