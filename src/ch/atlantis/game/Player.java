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
    private Hand hand;
    private PathCardStack pathCardStack;
    private GamePiece gamePieces;
    private String playerName;
    private Card bridge;
    private int playerId;

    public Player(int playerId) {
        this.playerId = playerId;
        this.pathCardStack = new PathCardStack();
        this.hand = new Hand();
        this.gamePieces = new GamePiece(playerId);
        this.gameName = gameName;
    }

    public String getPlayerName() { return playerName; }

    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public void addScore(int score){ this.score += score; }

    public void subtractScore(int score) { this.score -= score; }

    public int getScore() { return score; }

    public void addBridge(Card bridge) { this.bridge = bridge; }

    public void removeBridge(){ this.bridge = null; }

    public void addHandCard(Card handCard){ hand.addCard(handCard); }

    public void removeHandCard(Card handCard){ hand.removeCard(handCard); }

    public Card getHandCard(int index) { return hand.getHandCard(index); }

    public ArrayList<Card> getHandCards() { return hand.getHandCards(); }

    public int getHandCardSize() { return hand.getSize(); }

    public ArrayList<GamePiece> getGamePieces() { return gamePieces.getGamePieces(); }

    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public int getPlayerId() { return playerId; }

    public void addPathCard(Card card) { pathCardStack.addPathCard(card); }

    public void removePathCard(Card card) { pathCardStack.removePathCard(card); }

    public ArrayList<Card> getPathCardStack() { return pathCardStack.getPathCardStack(); }

    public int getPlayerId() {
        return playerId;
    }

    public String getGameName() {
        return gameName;
    }
}
