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
    private ArrayList<Card> pathCardSetA;
    private ArrayList<Card> pathCardSetB;
    private ArrayList<Card> deck;
    private Player localPlayer;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        this.localPlayer = localPlayer;

        if (message.getMessageObject() instanceof HashMap) {
            HashMap<String, ArrayList> initList = (HashMap<String, ArrayList>) message.getMessageObject();
            players = initList.get("Players");
            tiles = initList.get("Tiles");
            pathCardSetA = initList.get("PathCardsSetA");
            pathCardSetB = initList.get("PathCardsSetB");
            deck = initList.get("Deck");

//            System.out.println("Players: " + players.size() + "\n" +
//                    "Tiles: " + tiles.size() + "\n" +
//                    "PathA: " + pathCardSetA.size() + "\n" +
//                    "PathB: " + pathCardSetB.size() + "\n" +
//                    "Deck: "  + deck.size() + "\n");
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public ArrayList<Card> getPathCardSetA() {
        return pathCardSetA;
    }

    public ArrayList<Card> getPathCardSetB() {
        return pathCardSetB;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}
