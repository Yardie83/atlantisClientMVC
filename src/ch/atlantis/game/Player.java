package ch.atlantis.game;

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
    private ArrayList<Card> pathCardStack;
    private String playerName;
    private String gameName;
    private int playerID;
    private Color color;
    private int bridge;
    private int score;
    private int gamePiecesOnLand;

    public Player(int playerID, String gameName, String playerName) {
        this.playerName = playerName;
        this.playerID = playerID;
        this.gameName = gameName;
        this.movementCards = null;
        this.gamePieces = null;
    }

    public void applyColor() {
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

    public Color getColor() { return color; }

    public String getColorName(){
        switch (playerID) {
            case 0:
                return "red";

            case 1:
                return "blue";

            case 2:
                return "green";

            case 3:
                return "yellow";
            }
            return null;
    }

    public int getScore() { return score; }

    public int getBridge() {
        return bridge;
    }

    public void removeBridge() {
        this.bridge = 0;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }

    public ArrayList<Card> getMovementCards() { return movementCards; }

    public ArrayList<Card> getPathCardStack() {
        if (pathCardStack == null){
            pathCardStack = new ArrayList<>();
        }
        return pathCardStack;
    }
}
