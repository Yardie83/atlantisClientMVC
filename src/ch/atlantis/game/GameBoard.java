package ch.atlantis.game;

import ch.atlantis.view.AtlantisView;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Hermann Grieder on 23.08.2016.
 */
public class GameBoard extends Pane {

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
    private Card startField;
    private Card endField;

    public GameBoard(int height, int width, ArrayList<Player> players, AtlantisView view) {

        this.players = players;
        this.view = view;

        super.setMinHeight(height);
        super.setMinWidth(width);

        readLayout();
        createTiles(height);
        addPlayers();
        addPlayerPieces();
        createPathCardSets();
        createMovementCards();
        createWaterCards();
        createBridges();
        createStartField();
        createEndField();
        addHandCards();
        drawBoard();
    }

    private void createEndField() {
        endField = new Card(CardType.END);
    }

    private void createStartField() {
        startField = new Card(CardType.START);
    }

    private void createBridges() {
        for (Player player : players) {
            Card bridge = new Card(CardType.BRIDGE);
            player.addBridge(bridge);
            bridges.add(bridge);
        }
    }

    private void createWaterCards() {
        this.waterCards = new ArrayList<>();
        for (int i = 0; i < 24; i++){
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
        Color c;

        for (int x = 0; x < columnCount; x++) {
            for (int y = 0; y < rowCount; y++) {

                int xPos = x * side;
                int yPos = y * side;

                switch (tileTypeCodes[x][y]) {
                    case 0:
                        tileType = TileType.EMPTY;
                        c = Color.WHITE;
                        break;
                    case 1:
                        tileType = TileType.PATH;
                        c = Color.BROWN;
                        break;
                    case 2:
                        tileType = TileType.WATER;
                        c = Color.DARKBLUE;
                        break;
                    case 3:
                        tileType = TileType.START;
                        c = Color.GRAY;
                        break;
                    case 4:
                        tileType = TileType.END;
                        c = Color.GRAY;
                        break;
                    case 5:
                        tileType = TileType.HANDCARD;
                        c = Color.DEEPSKYBLUE;
                        break;
                    case 6:
                        tileType = TileType.BRIDGE;
                        c = Color.BISQUE;
                        break;
                    default:
                        tileType = TileType.EMPTY;
                        c = Color.BLACK;
                        break;
                }
                tiles.add(new Tile(xPos, yPos, side, tileType, pathId[x][y], c));
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
                            pathId[x][y] = value % 100;
                            value = 1;
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

    public void drawBoard() {
        Card card;
        Iterator<Card> iterA = pathCardsSetA.iterator();
        Iterator<Card> iterB = pathCardsSetB.iterator();

        for (Tile tile : tiles) {
            switch (tile.getTileType()){
                case PATH:
                    if (tile.getPathId() != 0 && tile.getPathId() < 28){
                        card = iterA.next();
                        drawCard(card, tile);
                    }
                    if (tile.getPathId() != 0 && tile.getPathId() > 27){
                        card = iterB.next();
                        drawCard(card, tile);
                    }
                    break;
                case WATER:
                    break;
                case BRIDGE:
                    break;
                case START:
                    break;
                case END:
                    break;
                case HANDCARD:
                    break;
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
        Stage gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(scene);
    }
}
