package ch.atlantis.game;

import javafx.scene.paint.Color;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */

enum TileType {
    //EMPTY = 0, PATH = 1, WATER = 2, START = 3, END = 4, HANDCARD = 5, BRIDGE = 6
    EMPTY, PATH, WATER, START, END, HANDCARD, BRIDGE
}

public class Tile {
    private int x;
    private int y;
    private int side;
    private TileType tileType;
    private int pathId;
    private Color c;

    public Tile(int x, int y, int side, TileType tileType, int pathId, Color c) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.tileType = tileType;
        this.pathId = pathId;
        this.c = c;
    }

    public TileType getTileType() {
        return tileType;
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

    public int getPathId() {
        return pathId;
    }

    public Color getColor() {
        return c;
    }
}