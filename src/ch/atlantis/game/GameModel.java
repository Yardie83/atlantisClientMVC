package ch.atlantis.game;

import ch.atlantis.util.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hermann Grieder on 28.10.2016
 */
public class GameModel {

    private ArrayList<Player> players;
    private ArrayList<Tile> tiles;
    private ArrayList<Card> pathCards;
    private ArrayList<Card> deck;
    private Player localPlayer;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        if (message.getMessageObject() instanceof HashMap) {
            HashMap<String, ArrayList> initList = (HashMap<String, ArrayList>) message.getMessageObject();
            players = initList.get("Players");
            tiles = initList.get("Tiles");
            pathCards = initList.get("PathCards");
            deck = initList.get("Deck");

            for (Player player : players) {
                player.applyColor();
                if (player.getPlayerID() == localPlayer.getPlayerID()) {
                    this.localPlayer = player;
                }
            }
        }
    }


    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}
