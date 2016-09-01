package ch.atlantis.game;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * Created by Hermann Grieder on 23.08.2016.
 */
public class GameBoard extends Pane {

    private int gridCount = 12;
    private Tile[][] tiles;

    public GameBoard(int heightValue, int widthValue) {

        int side = widthValue / (widthValue / (heightValue / gridCount));
        int columnCount = widthValue / side;
        Random rand = new Random();
        tiles = new Tile[columnCount][gridCount];

        for (int x = 0; x < columnCount; x++) {
            for (int y = 0; y < gridCount; y++) {
                tiles[x][y] = new Tile(x * side, y * side, side, Tiletype.EMPTY);
                System.out.println("X: " + x + " " + "Y: " + y);
                Rectangle rect = new Rectangle();
                rect.setWidth(side);
                rect.setHeight(side);
                rect.setFill(Color.rgb((int) (rand.nextFloat() * 255), (int) (rand.nextFloat() * 255), (int) (rand.nextFloat() * 255)));
                rect.setX(x * side);
                rect.setY(y * side);
                this.getChildren().add(rect);
            }
        }

//        for (int i = 0; i < gridCount; i++) {
//            Line horizontalLine = new Line();
//            horizontalLine.setStartX(0);
//            horizontalLine.setEndX(widthValue);
//            horizontalLine.setStartY(i * side);
//            horizontalLine.setEndY(i * side);
//            horizontalLine.setStroke(Color.BLACK);
//
//            this.getChildren().addAll(horizontalLine);
//        }
//
//        for (int i = 0; i * side < widthValue; i++) {
//            Line verticalLine = new Line();
//            verticalLine.setStartY(0);
//            verticalLine.setEndY(heightValue);
//            verticalLine.setStartX(i * side);
//            verticalLine.setEndX(i * side);
//            verticalLine.setStroke(Color.BLACK);
//
//            this.getChildren().addAll(verticalLine);
//        }
    }
}
