package ch.atlantis.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 03.09.2016.
 */
public class GamePiece extends Rectangle {

    private int pieceId;
    private int playerId;
    private double x;
    private double y;
    private ArrayList<GamePiece> gamePieces;
    private int pathId;

    public GamePiece(int playerId, int pieceId) {
        this.playerId = playerId;
        setColorSet(playerId);
        setPieceId(pieceId);

        this.setStroke(Color.BLACK);
        this.setWidth(10);
        this.setHeight(10);
    }

    public GamePiece(int playerId) {
        gamePieces = new ArrayList<>(4);
        createGamePieces(playerId);
    }

    private void createGamePieces(int playerId) {
        for (int i = 0; i < 3; i++) {
            this.gamePieces.add(new GamePiece(playerId, i));
        }
    }

    public void setColorSet(int colorSet) {
        switch (colorSet) {
            case 0:
                this.setFill(Color.RED);
                break;
            case 1:
                this.setFill(Color.BLUE);
                break;
            case 2:
                this.setFill(Color.PINK);
                break;
            case 3:
                this.setFill(Color.GREEN);
                break;
        }
    }

    public void moveGamePiece(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.x = x;
        this.y = y;
    }

    public void setGamePiecePathId(int pathId) {
        this.pathId = pathId;
    }

    public int getGamePiecePathId() {
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
