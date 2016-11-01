package ch.atlantis.game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 7661939850705259874L;
    private ArrayList<GamePiece> gamePieces;
    private ArrayList<Card> movementCards;
    private int playerID;
    private String playerName;
    private String gameName;
    private int score;
    private int bridge;

    public Player(int playerID, String gameName) {
        this.playerID = playerID;
        this.gameName = gameName;

        this.movementCards = new ArrayList<>();
        this.gamePieces = new ArrayList<>(4);

        this.bridge = 1;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void subtractScore(int score) {
        this.score -= score;
    }

    public int getScore() {
        return score;
    }

    public void removeBridge() {
        this.bridge = 0;
    }

    public int getBridge() {
        return bridge;
    }

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getGameName() {
        return gameName;
    }
}
