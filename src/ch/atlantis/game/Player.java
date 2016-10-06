package ch.atlantis.game;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 */
public class Player {
    private int score = 0;
    private int playerID;
    private String playerName;
    private Color playerColor;
    private ArrayList<GamePiece> gamePieces;
    private ArrayList<Card> pathCards;
    private ArrayList<Card> movementCards;
    private Card bridge;

    public Player(String playerName, Color playerColor) {
        this.playerName = playerName;
        this.playerColor = playerColor;
        this.pathCards = new ArrayList<>();
        this.movementCards = new ArrayList<>();
        this.gamePieces = new ArrayList<>(4);
    }

    public String getPlayerName() { return playerName; }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void addScore(int score){
        this.score += score;
    }

    public void subtractScore(int score) { this.score -= score; }

    public int getScore() { return score; }

    public void addGamePiece(GamePiece gamePiece) {
        this.gamePieces.add(gamePiece);
    }

    public void addBridge(Card bridge) {
        this.bridge = bridge;
    }

    public void removeBridge(){
        this.bridge = null;
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
    }

    public void addPathCard(Card pathCard) {
        this.pathCards.add(pathCard);
    }

    public void removePathCard(Card pathCard){
        this.pathCards.remove(pathCard);
    }

    public ArrayList<Card> getMovementCards() {
        return movementCards;
    }

    public void addHandCard(Card handCard){
        this.pathCards.add(handCard);
    }

    public void removeHandCard(Card handCard){
        this.pathCards.remove(handCard);
    }

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }
}
