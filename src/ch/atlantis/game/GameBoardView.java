package ch.atlantis.game;

import ch.atlantis.view.AtlantisView;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Hermann Grieder on 23.08.2016.
 */
public class GameBoardView extends Pane {

    private int columnCount = 20;
    private int rowCount = 11;
    private int[][] pathId;
    private int[][] tileTypeCodes;
    private AtlantisView view;
    private ArrayList<Tile> tiles;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCardsSetA;
    private ArrayList<Card> pathCardsSetB;
    private ArrayList<Card> movementCards;
    private ArrayList<Card> waterCards;
    private ArrayList<Card> bridges;
    private ArrayList<Card> pathArray;
    private Stage gameStage;

    public GameBoardView(ArrayList<Player> players, AtlantisView view) {

        this.players = players;
        this.view = view;

        super.setMinHeight(view.heightProperty().getValue());
        super.setMinWidth(view.widthProperty().getValue());


        readLayout();
        createTiles(view.heightProperty().getValue());
        addPlayers();
        addPlayerPieces();
        createPathCardSets();
        createMovementCards();
        createWaterCards();
        createBridges();
        addHandCards();
        drawBoard();
    }

    private void createBridges() {
        this.bridges = new ArrayList<>(4);
        for (Player player : players) {
            Card bridge = new Card(CardType.BRIDGE);
            player.addBridge(bridge);
            bridges.add(bridge);
        }
    }

    private void createWaterCards() {
        this.waterCards = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            waterCards.add(new Card(CardType.WATER));
        }
    }

    private void addPlayers() {
    }

    private void addHandCards() {

    }

    private void createMovementCards() {
        this.movementCards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 15; j++) {
                movementCards.add(new Card(i, CardType.MOVEMENT));
            }
        }
    }

    private void createPathCardSets() {
        this.pathCardsSetA = new ArrayList<>();
        this.pathCardsSetB = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 1; k <= 7; k++) {
                    if (i == 0) {
                        pathCardsSetA.add(new Card(j, k, CardType.PATH));
                    }
                    if (i == 1) {
                        pathCardsSetB.add(new Card(j, k, CardType.PATH));
                    }
                }
            }
        }
    }

    private void addPlayerPieces() {
        for (Player player : players) {
            for (int i = 0; i < 3; i++) {
                GamePiece gamePiece = new GamePiece(i, player);
                player.addGamePiece(gamePiece);
            }
        }
    }

    private void removePlayerPiece(Player player, int pieceId) {

    }


    private void createTiles(int height) {
        int side = (height / rowCount);

        tiles = new ArrayList<>(columnCount * rowCount);
        TileType tileType;


        for (int x = 0; x < columnCount; x++) {
            for (int y = 0; y < rowCount; y++) {

                int xPos = x * side;
                int yPos = y * side;

                switch (tileTypeCodes[x][y]) {
                    case 0:
                        tileType = TileType.EMPTY;
                        break;
                    case 1:
                        tileType = TileType.PATH;
                        break;
                    case 2:
                        tileType = TileType.WATER;
                        break;
                    case 3:
                        tileType = TileType.START;
                        break;
                    case 4:
                        tileType = TileType.END;
                        break;
                    case 5:
                        tileType = TileType.HANDCARD;
                        break;
                    case 6:
                        tileType = TileType.BRIDGE;
                        break;
                    default:
                        tileType = TileType.EMPTY;
                        break;
                }
                tiles.add(new Tile(xPos, yPos, side, tileType, pathId[x][y]));
            }
        }
    }

    private void readLayout() {

        pathId = new int[columnCount][rowCount];

        //columnCount = 15; rowCount = 10
        tileTypeCodes = new int[columnCount][rowCount];
        try {
            BufferedReader bf = new BufferedReader(new FileReader("src/ch/atlantis/res/GameBoardLayout.txt"));

            String currentLine;
            int y = -1;
            try {
                while ((currentLine = bf.readLine()) != null) {
                    y++;
                    String[] values = currentLine.trim().split(" ");
                    for (int x = 0; x < values.length; x++) {
                        int value = Integer.parseInt(values[x]);
                        if ((value / 100) == 1) {
                            pathId[x][y] = value;
                            value = 1;
                        } else if (value / 100 == 2) {
                            pathId[x][y] = value;
                            value = 2;
                        } else if (value / 100 == 3) {
                            pathId[x][y] = value;
                            value = 3;
                        } else if (value / 100 == 4) {
                            pathId[x][y] = value;
                            value = 4;
                        } else if (value / 100 == 5) {
                            pathId[x][y] = value;
                        } else {
                            pathId[x][y] = 0;
                            value = value / 100;
                        }
                        tileTypeCodes[x][y] = value;
                    }
                }
            } catch (IOException e) {
                System.out.println("Empty Line!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File \"GameBoardLayout.txt\" not found!");
        }
    }

    private void createGameConsole(Tile tile) {

        VBox console = new VBox();

        console.setStyle("-fx-border-width: 1px");


        Label label1 = new Label("hallo");

        HBox top = new HBox();

        top.getChildren().addAll(label1);

        HBox bottom = new HBox();

        console.getChildren().addAll(top, bottom);

        console.setLayoutX(tile.getX());
        console.setLayoutY(tile.getY());
        console.setMinHeight(200);
        console.setMinWidth(200);

        this.getChildren().add(console);
    }

    public void drawBoard() {
        Card card = null;
        Iterator<Card> iterWater = waterCards.iterator();
        Iterator<Card> iterA = pathCardsSetA.iterator();
        Iterator<Card> iterB = pathCardsSetB.iterator();

        pathArray = new ArrayList<>();

        for (Tile tile : tiles) {

            int pathId = tile.getPathId();

            if (pathId != 0) {

                // Place two cards from 1 to 10 and from 17 to 27 from Card set A
                if ((pathId <= 110 || pathId >= 121) && pathId <= 126) {
                    for (int i = 0; i < 2; i++) {
                        card = iterA.next();
                    }
                }
                // Place only one card from 11 to 20 from Card set A
                if (pathId >= 111 && pathId <= 120) {
                    card = iterA.next();
                }

                //Water card at path id 227 in the middle of the board
                if (pathId == 227) {
                    card = iterWater.next();
                }

                //Start card
                if (pathId == 300) {
                    card = new Card(CardType.START);
                }

                //End card
                if (pathId == 400) {
                    card = new Card(CardType.END);
                }

                //End card
                if (pathId == 500) {
                    createGameConsole(tile);
                    System.out.println("call");;
                }

                // Card 27 is water. Place two cards from 28 to 33 and from 44 to 53 from Card set B
                if ((pathId >= 128 && (pathId <= 133) || (pathId >= 144)) && pathId <= 200) {
                    for (int i = 0; i < 2; i++) {
                        card = iterB.next();
                    }
                }
                // Place only one card from 34 to 43 from Card set B
                if (pathId >= 134 && pathId <= 143) {
                    card = iterB.next();
                }

                card.addPathID(pathId);
                drawCard(card, tile);
                pathArray.add(card);
            }
        }
    }

    private void drawCard(Card card, Tile tile) {
        card.setWidth(tile.getSide());
        card.setHeight(tile.getSide());
        card.setLayoutX(tile.getX());
        card.setLayoutY(tile.getY());
        this.getChildren().add(card);
    }

    public void show() {
        Scene scene = new Scene(this);
        gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(scene);
    }
}
