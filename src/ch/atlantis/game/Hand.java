package ch.atlantis.game;

import java.util.ArrayList;

/**
 * Created by Fabian on 30/08/16.
 */
public class Hand {

    private ArrayList<Card> hand;

    public Hand() { this.hand = new ArrayList<>(); }

    public void clear() { this.hand.clear(); }

    public void addCard(Card card) { this.hand.add(card); }

    public void removeCard(Card card) { this.hand.remove(card); }

    public ArrayList<Card> getHandCards() { return hand; }

}
