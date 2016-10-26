package ch.atlantis.game;

import java.util.ArrayList;

/**
 * Created by Fabian on 30/08/16.
 */
public class Deck {

    private ArrayList<Card> deck;
    private ArrayList<Card> collected;

    public Deck() {
        this.deck = new ArrayList<>(105);
        this.collected = new ArrayList<>();
    }

    public void collectCard(Card card) { collected.add(card); }

    public void fillDeckFromCollected() {
        if (deck.size() == 0) {
            for (int i = 0; i < collected.size(); i++) {
                Card tempCard = collected.get(i);
                deck.add(tempCard);
                collected.remove(i);
            }
        }
    }

    public void fillDeckToStart(ArrayList<Card> movementCards) {
        for (int i = 0; i < movementCards.size(); i++) {
            Card tempCard = movementCards.get(i);
            deck.add(tempCard);
            movementCards.remove(i);
        }
    }

    public Card getDeckCard() {
        Card tempCard = deck.get(0);
        deck.remove(0);
        return tempCard;
    }

}
