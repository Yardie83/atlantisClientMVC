package ch.atlantis.game;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

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
    private Color color;

    public Player(int playerID, String gameName) {
        this.playerID = playerID;
        this.gameName = gameName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void subtractScore(int score) {
        this.score -= score;
    }

    public int getScore() { return score; }

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

    public Color getColor() { return color; }

    public ArrayList<Card> getMovementCards() { return movementCards; }

    public void setColor() {
        switch (playerID) {
            case 0:
                color = Color.RED;
                break;
            case 1:
                color = Color.BLUE;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.YELLOW;
                break;
        }
    }

    public void setBridge() { this.bridge = 1; }

    public void setName(String name) {
        this.playerName = name;
    }
}
