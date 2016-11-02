package ch.atlantis.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 03.09.2016.
 */
public class GamePiece extends Rectangle implements Serializable {

    private static final long serialVersionUID = 7661939850705259125L;
    private int pieceId;
    private int playerId;
    private double x;
    private double y;
    private ArrayList<GamePiece> gamePieces;
    private int pathId;

    public void moveGamePiece(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.x = x;
        this.y = y;
    }

    public void setPathId(int pathId) {
        this.pathId = pathId;
    }

    public int getPathId() {
        return pathId;
    }

    public int getPieceId() {
        return pieceId;
    }

    public void setPieceId(int pieceId) {
        this.pieceId = pieceId;
    }

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }

    public int getPlayerId() {
        return playerId;
    }

    public double getGamePieceX() {
        return x;
    }

    public double getGamePieceY() {
        return y;
    }

}
