package ch.atlantis.game;

import javafx.scene.shape.Rectangle;

/**
 * Created by Hermann Grieder on 03.09.2016.
 */
public class GamePiece extends Rectangle {

    private int pieceId;
    private Player player;
    private double x;
    private double y;
    private ArrayList<GamePiece> gamePieces;

    public GamePiece(int pieceId, Player player) {
        this.pieceId = pieceId;
        this.player = player;
       // this.setFill(player.getPlayerColor());
        this.setWidth(10);
        this.setHeight(10);
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
