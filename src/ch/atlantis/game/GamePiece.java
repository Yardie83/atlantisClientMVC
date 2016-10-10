package ch.atlantis.game;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 03.09.2016.
 */
public class GamePiece extends Rectangle {

    private int pieceId;
    private Player player;
    private double x;
    private double y;
    private ArrayList<GamePiece> gamePieces;

    public GamePiece(int playerId) {
        gamePieces = new ArrayList<>(4);
        createGamePieces(playerId);
    }

    private void createGamePieces(int playerId) {
        for (int i = 0; i < 4; i++) {

        }
    }

    public int getPieceId() {
        return pieceId;
    }

    public Player getPlayer() {
        return player;
    }

    public void moveGamePiece(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.x = x;
        this.y = y;
    }

    public double getGamePieceX() { return x; }
    public double getGamePieceY() { return y; }

}
