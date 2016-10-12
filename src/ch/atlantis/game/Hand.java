package ch.atlantis.game;

import java.util.ArrayList;

/**
 * Created by Fabian on 30/08/16.
 */
public class Hand {

    private ArrayList<Card> handCards;

    public Hand() {
        this.handCards = new ArrayList<>();
    }

    public void clear() { this.handCards.clear(); }

    public void addCard(Card card) { this.handCards.add(card); }

    public void removeCard(Card card) { this.handCards.remove(card); }

    public Card getHandCard(int index) { return this.handCards.get(index); }

    public int getSize() { return this.handCards.size(); }

    public ArrayList<Card> getHandCards() { return handCards; }

}
