package ch.atlantis.game;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */

enum Tiletype {
    EMPTY, WATER, PATH, START, END
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



}