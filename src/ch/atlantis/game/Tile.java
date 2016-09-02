package ch.atlantis.game;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */

enum Tiletype {
    //EMPTY = 0, PATH = 1, WATER = 2, START = 3, END = 4, CARD = 5, BRIDGE = 6
    EMPTY, PATH, WATER, START, END, CARD, BRIDGE
}

public class Tile {
    private int x;
    private int y;
    private int side;
    private Tiletype tiletype;

    public Tile(int x, int y, int side, Tiletype tiletype) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.tiletype = tiletype;
    }

    public Tiletype getTiletype() {
        return tiletype;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSide() {
        return side;
    }
}