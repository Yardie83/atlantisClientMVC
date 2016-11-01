package ch.atlantis.game;

import java.io.Serializable;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */

public class Tile implements Serializable {
    private static final long serialVersionUID = 7661939850705259952L;
    private int pathId;
    private int x;
    private int y;
    private int side;


    public Tile(int x, int y, int pathId) {
        this.x = x;
        this.y = y;

        this.pathId = pathId;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getPathId() {
        return pathId;
    }

    public void setPathId(int pathId) {
        this.pathId = pathId;
    }
}
