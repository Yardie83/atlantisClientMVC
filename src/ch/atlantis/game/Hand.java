package ch.atlantis.game;

import java.util.ArrayList;

/**
 * Created by Fabian on 30/08/16.
 */
public class Hand {

    private ArrayList<Card> handCards;
    private Player player;

    public Hand(Player player) {
        this.player = player;
        this.handCards = new ArrayList<>();
    }

    public void clear() { this.handCards.clear(); }

    public void addCard(Card card) { this.handCards.add(card); }

    public void removeCard(Card card) { this.handCards.remove(card); }

    public ArrayList<Card> getHandCards() { return handCards; }

    public String getPlayer() { return player.getPlayerName(); }

}
