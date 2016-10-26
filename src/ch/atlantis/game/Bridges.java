package ch.atlantis.game;

import java.util.ArrayList;

/**
 * Created by Fabian on 17/10/16.
 */
public class Bridges {

    private ArrayList<Card> bridges;
    private int playerId;

    public Bridges(int playerId) {
        this.playerId = playerId;
        this.bridges = new ArrayList<>();
    }

    public void addBridges(Card bridge) { this.bridges.add(bridge); }

    public void clear() { this.bridges.clear(); }

    public void removeBridge(Card bridge) { this.bridges.remove(bridge); }

    public ArrayList<Card> getBridges() { return this.bridges; }

}
