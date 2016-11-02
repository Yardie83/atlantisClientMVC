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
    private ArrayList pathCards;
    private ArrayList<Card> deck;
    private Player localPlayer;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        this.localPlayer = localPlayer;

        if (message.getMessageObject() instanceof HashMap) {
            HashMap<String, ArrayList> initList = (HashMap<String, ArrayList>) message.getMessageObject();
            players = initList.get("Players");
            tiles = initList.get("Tiles");
            pathCards = initList.get("PathCards");
            deck = initList.get("Deck");

            initPlayer(players);

//            System.out.println("Players: " + players.size() + "\n" +
//                    "Tiles: " + tiles.size() + "\n" +
//                    "PathA: " + pathCardSetA.size() + "\n" +
//                    "PathB: " + pathCardSetB.size() + "\n" +
//                    "Deck: "  + deck.size() + "\n");
        }
    }

    private void initPlayer(ArrayList<Player> players) {
        for (Player player : players) {
            player.setBridge();
            player.setColor();
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
