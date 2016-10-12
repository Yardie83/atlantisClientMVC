package ch.atlantis.game;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Fabian on 15/08/16.
 *
 */
public class Player {
    private String gameName;
    private int score = 0;
    private ArrayList<Card> pathCards;
    private ArrayList<Card> movementCards;
    private ArrayList<Card> handCards;
    private Hand hand;
    private GamePiece gamePiece;
    private String playerName;
    private Card bridge;
    private int playerId;

    public Player(int playerId) {
        this.playerId = playerId;
        this.pathCards = new ArrayList<>();
        this.movementCards = new ArrayList<>();
        this.hand = new Hand();
        this.gamePiece = new GamePiece()
        this.gameName = gameName;
    }

    public String getPlayerName() { return playerName; }

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
        hand.addCard(handCard);
    }

    public void removeHandCard(Card handCard){
        hand.removeCard(handCard);
    }

    public Card getHandCard(int index) { return hand.getHandCard(index); }

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }

    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public int getPlayerId() {
        return playerId;
    }

    public String getGameName() {
        return gameName;
    }
}
