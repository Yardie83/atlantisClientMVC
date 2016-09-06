package ch.atlantis.game;

import ch.atlantis.view.AtlantisView;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Hermann Grieder on 23.08.2016.
 *
 */
public class GameBoardView extends Pane {

    private int rowCount = 11;
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
    private Scene gameScene;

    public GameBoardView(ArrayList<Player> players, AtlantisView view) {

        this.players = players;
        this.view = view;

        int height = view.heightProperty().getValue();
        int width = view.widthProperty().getValue();

        super.setMinHeight(height);
        super.setMinWidth(width);

        super.setBackground(new Background(new BackgroundImage(
                new Image("ch/atlantis/res/gameboardbg.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));

        readLayout(height);
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
            Card bridge = new Card(7,CardType.BRIDGE);
            player.addBridge(bridge);
            bridges.add(bridge);
        }
    }

    private void createWaterCards() {
        this.waterCards = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            waterCards.add(new Card(5,CardType.WATER));
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
                    else{
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

    private void readLayout(int height) {

        int pathId;
        int side = (height / rowCount);

        tiles = new ArrayList<>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader("src/ch/atlantis/res/GameBoardLayout.txt"));

            String currentLine;
            int y = -1;

            try {
                while ((currentLine = bf.readLine()) != null) {
                    y++;
                    String[] values = currentLine.trim().split(" ");
                    for (int x = 0; x < values.length; x++) {

                        int xPos = x * side;
                        int yPos = y * side;

                        int value = Integer.parseInt(values[x]);

                        if ((value / 100) >= 1 && (value / 100) <= 5) {
                            pathId = value;
                        } else {
                            pathId = 0;
                        }
                        tiles.add(new Tile(xPos, yPos, side, pathId));
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
        Card card = null;
        Iterator<Card> iterWater = waterCards.iterator();
        Iterator<Card> iterA = pathCardsSetA.iterator();
        Iterator<Card> iterB = pathCardsSetB.iterator();

        pathArray = new ArrayList<>();

        for (Tile tile : tiles) {

            int pathId = tile.getPathId();

            if (pathId != 0 && pathId != 500) {

                // Place two cards from 101 to 110 and from 120 to 126 from Card set A
                if ((pathId <= 110 || pathId >= 121) && pathId <= 126) {
                    for (int i = 0; i < 2; i++) {
                        card = iterA.next();
                    }
                }
                // Place only one card from 111 to 120 from Card set A
                else if (pathId >= 111 && pathId <= 120) {
                    card = iterA.next();
                }

                //Water card at path id 227 in the middle of the board
                else if (pathId == 227) {
                    card = iterWater.next();
                }

                //Start card
                else if (pathId == 300) {
                    card = new Card(3,CardType.START);
                }

                //End card
                else if (pathId == 400) {
                    card = new Card(4,CardType.END);
                }

                // Card 127 is water. Place two cards from 128 to 133 and from 144 to 153 from Card set B
                else if ((pathId >= 128 && (pathId <= 133) || (pathId >= 144)) && pathId <= 200) {
                    for (int i = 0; i < 2; i++) {
                        card = iterB.next();
                    }
                }
                // Place only one card from 134 to 143 from Card set B
                else if (pathId >= 134 && pathId <= 143) {
                    card = iterB.next();
                }

                card.setPathId(pathId);
                drawCard(card, tile);
                pathArray.add(card);
            }

            //Console will be placed at these X and Y coordinates
            if (pathId == 500) {
                createGameConsole(tile);
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

    private void createGameConsole(Tile tile) {

        VBox console = new VBox();

        console.setStyle("-fx-border-width: 1px; -fx-background-color: #7af5c4;" +
                "-fx-border-color: black");


        Label label1 = new Label("hallo");

        HBox top = new HBox();

        top.getChildren().add(label1);

        HBox bottom = new HBox();

        console.getChildren().addAll(top, bottom);

        console.setLayoutX(tile.getX());
        console.setLayoutY(tile.getY()+20);
        console.setMinHeight(200);
        console.setMinWidth(tile.getSide()*7);

        this.getChildren().add(console);
    }

    public void show() {
        gameScene = new Scene(this);
        gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(gameScene);
    }

    public ArrayList<Card> getPathArray() {
        return pathArray;
    }

    public Stage getGameStage() {
        return gameStage;
    }


}
