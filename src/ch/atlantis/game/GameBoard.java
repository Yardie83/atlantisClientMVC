package ch.atlantis.game;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Hermann Grieder on 23.08.2016.
 */
public class GameBoard extends Pane {

    private int columnCount = 15;
    private int rowCount = 10;
    private Tile[][] tiles;

    public GameBoard(int heightValue, int widthValue) {

        int side = widthValue / (widthValue / (heightValue / rowCount));
        Random rand = new Random();
        tiles = new Tile[columnCount][rowCount];
        int[][] tileTypeCode = readLayout();

        Tiletype tiletype;
        Color c = Color.ANTIQUEWHITE;

        for (int x = 0; x < columnCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                switch (tileTypeCode[x][y]) {
                    case 0:
                        tiletype = Tiletype.EMPTY;
                        c = Color.ANTIQUEWHITE;
                        break;
                    case 1:
                        tiletype = Tiletype.PATH;
                        c = Color.BROWN;
                        break;
                    case 2:
                        tiletype = Tiletype.WATER;
                        c = Color.DARKBLUE;
                        break;
                    case 3:
                        tiletype = Tiletype.START;
                        c = Color.GRAY;
                        break;
                    case 4:
                        tiletype = Tiletype.END;
                        c = Color.GRAY;
                        break;
                    case 5:
                        tiletype = Tiletype.CARD;
                        break;
                    case 6:
                        tiletype = Tiletype.BRIDGE;
                        c = Color.BISQUE;
                        break;
                    default:
                        tiletype = Tiletype.EMPTY;
                        c = Color.ANTIQUEWHITE;
                        break;
                }

                tiles[x][y] = new Tile(x * side, y * side, side, tiletype);
                Rectangle rect = new Rectangle();
                rect.setWidth(side);
                rect.setHeight(side);




                rect.setFill(c);
                rect.setX(x * side);
                rect.setY(y * side);
                this.getChildren().add(rect);
            }
        }
    }

    private int[][] readLayout() {
        int[][] tileTypeCodes = new int[columnCount][rowCount];
        try {
            BufferedReader bf = new BufferedReader(new FileReader("src/ch/atlantis/res/GameBoardLayout.txt"));

            String currentLine;
            try {
                while ((currentLine = bf.readLine()) != null) {
                    String[] values = currentLine.trim().split(" ");
                    for (int x = 0; x < rowCount; x++) {
                        for (int y = 0; y < values.length; y++) {
                            tileTypeCodes[x][y] = Integer.parseInt(values[y]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Empty Line!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File \"GameBoardLayout.txt\" not found!");
        }
        return tileTypeCodes;
    }
}