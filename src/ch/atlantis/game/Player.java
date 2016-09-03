package ch.atlantis.game;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Player {
    private int score;
    private int playerID;
    private String playerName;
    private Color playerColor;
    private ArrayList<GamePiece> gamePieces;
    private ArrayList<Card> handCards;
    private Card bridge;

    public Player(String playerName, Color playerColor) {
        this.playerName = playerName;
        this.playerColor = playerColor;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void addScore(int score){
        this.score += score;
    }

    public void addGamePiece(GamePiece gamePiece) {
        this.gamePieces.add(gamePiece);
    }

    public void addBridge(Card bridge) {
        this.bridge = bridge;
    }
}
