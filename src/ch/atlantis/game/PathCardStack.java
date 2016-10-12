package ch.atlantis.game;

import java.util.ArrayList;

/**
 * Created by Fabian on 11/10/16.
 */
public class PathCardStack {

    private ArrayList<Card> pathCardStack;

    public PathCardStack() { pathCardStack = new ArrayList<>(); }

    public void clear() { this.pathCardStack.clear(); }

    public void addPathCard(Card card) { this.pathCardStack.add(card); }

    public void removePathCard(Card card) { this.pathCardStack.remove(card); }

    public Card getPathCard(int index) { return this.pathCardStack.get(index); }

    public ArrayList<Card> getPathCardStack() { return this.pathCardStack; }

}
