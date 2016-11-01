package ch.atlantis.game;

import ch.atlantis.util.Language;
import ch.atlantis.view.AtlantisView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Hermann Grieder on 23.08.2016.
 * <p>
 * The gameBoard that will be shown on game start.
 */
public class GameBoardView extends Pane {

    private AtlantisView view;
    private ArrayList<Tile> tiles;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCardsSetA;
    private ArrayList<Card> pathCardsSetB;
    private ArrayList<Card> pathCards;
    private Stage gameStage;
    private ArrayList<Card> deck;
    private int height;
    private final Player localPlayer;

    public GameBoardView(GameModel gameModel, AtlantisView view) {

        this.view = view;

        height = view.heightProperty().getValue();
        int width = view.widthProperty().getValue();

        super.setMinHeight(height);
        super.setMinWidth(width);

        localPlayer = gameModel.getLocalPlayer();
        players = gameModel.getPlayers();
        tiles = setXYTiles(gameModel.getTiles());
        pathCardsSetA = gameModel.getPathCardSetA();
        pathCardsSetB = gameModel.getPathCardSetB();
        pathCards = new ArrayList<>();
        deck = gameModel.getDeck();

        drawHand();

        drawBoard();

        drawGamePieces();
    }

    private ArrayList<Tile> setXYTiles(ArrayList<Tile> tiles) {
        int rowCount = 11;
        int side = (height / rowCount);

        for (Tile tile : tiles) {
            tile.setX(tile.getX() * side);
            tile.setY(tile.getY() * side);
            tile.setSide(side);
        }
        return tiles;
    }


    private void drawHand() {

    }


    /**
     * Draws the GameBoard by placing the cards on the corresponding tile
     * <p>
     * Hermann Grieder
     */
    private void drawBoard() {

        Iterator<Card> iteratorA = pathCardsSetA.iterator();
        Iterator<Card> iteratorB = pathCardsSetB.iterator();

        for (Tile tile : tiles) {

            int pathId = tile.getPathId();

            //Fill the path with water cards before adding the pathCards
            if (pathId >= 101 && pathId <= 153) {
                placeSpecialCard(Card.BLUE, CardType.WATER, tile);
            }

            if (pathId != 0 && pathId != 500) {

                // Place two cards from 101 to 110 and from 120 to 126
                // Place one card from 111 to 120 from Card set A
                if (pathId <= 126) {
                    if (!(pathId >= 111 && pathId <= 120)) {
                        placeTwoCards(iteratorA, tile);
                    } else {
                        placeOneCard(iteratorA, tile);
                    }
                }

                // Place two cards from 128 to 133 and from 144 to 153
                // Place one card from 134 to 143 from Card set B
                else if (pathId >= 128 && pathId <= 154) {
                    if (!(pathId >= 134 && pathId <= 143)) {
                        placeTwoCards(iteratorB, tile);
                    } else {
                        placeOneCard(iteratorB, tile);
                    }
                }
                //Start card
                else if (pathId == 300) {
                    placeSpecialCard(Card.YELLOW, CardType.START, tile);
                }
                //End card
                else if (pathId == 400) {
                    placeSpecialCard(Card.GREEN, CardType.END, tile);
                }
            }
            //Console will be placed at X and Y coordinates of the tile with the pathId 500
            if (pathId == 500) {
                createGameConsole(tile);
            }
            //Card deck will be placed at X and Y coordinates of the tile with the pathId 500
            if (pathId == 701) {
                createDeck(tile);
            }
        }
    }

    private void createDeck(Tile tile) {
        VBox deckBox = new VBox();

        deckBox.setStyle("-fx-border-width: 1px; " +
                "-fx-background-color: #F0F8FF;" +
                "-fx-border-color: black");


        Label label1 = new Label("Deck");

        deckBox.getChildren().add(label1);

        deckBox.setLayoutX(tile.getX());
        deckBox.setLayoutY(tile.getY());
        deckBox.setMinHeight(100);
        //deckBox.setMinWidth(tile.getSide() * 2);

        this.getChildren().add(deckBox);
    }


    private void placeSpecialCard(int colorSet, CardType cardType, Tile tile) {
        Card card = new Card(colorSet, cardType);
        card.setIsOnTop(true);
        drawCard(card, tile);
    }

    private void placeOneCard(Iterator<Card> iterator, Tile tile) {
        Card card = iterator.next();
        card.setIsOnTop(true);
        drawCard(card, tile);
    }

    private void placeTwoCards(Iterator<Card> iterator, Tile tile) {
        Card card;
        for (int i = 0; i < 2; i++) {
            card = iterator.next();
            if (i == 0) {
                card.setIsOnTop(false);
            } else {
                card.setIsOnTop(true);
            }
            drawCard(card, tile);
        }
    }

    private void drawCard(Card card, Tile tile) {

        card.setPathId(tile.getPathId());
        card.setWidth(tile.getSide());
        card.setHeight(tile.getSide());
        card.setLayoutX(tile.getX());
        card.setLayoutY(tile.getY());
        card.applyColor();

        pathCards.add(card);
        this.getChildren().add(card);
    }

    private void drawGamePieces() {
        int offsetX = 10;
        int offsetY = 5;
        for (Card card : pathCards) {
            if (card.getCardType() == CardType.START) {
                for (Player player : players) {
                    for (GamePiece gamePiece : player.getGamePieces()) {
                        gamePiece.setLayoutX(card.getLayoutX() + offsetX);
                        gamePiece.setLayoutY(card.getLayoutY() + offsetY);
                        this.getChildren().add(gamePiece);
                        offsetX += 20;
                    }
                    offsetY += 15;
                    offsetX = 10;
                }
            }
        }
    }

    private void createGameConsole(Tile tile) {

        VBox console = new VBox();
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        HBox bottom = new HBox(10);

        console.getChildren().addAll(top, bottom);

        console.setStyle("-fx-border-width: 1px; " +
                "-fx-background-color: #7af5c4;" +
                "-fx-border-color: black");


        String score = Integer.toString(localPlayer.getScore());
        Label label1 = new Label(localPlayer.getPlayerName());
        Label label2 = new Label("Score: " + score);
        Label label3 = new Label("|");

        top.getChildren().addAll(label1, label3, label2);

        console.setLayoutX(tile.getX());
        console.setLayoutY(tile.getY() + 20);
        console.setMinHeight(200);
        //console.setMinWidth(tile.getSide() * 7);

        this.getChildren().add(console);
    }

    public void show() {
        Scene gameScene = new Scene(this);
        gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(gameScene);
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
    }

    public Stage getGameStage() {
        return gameStage;
    }

    public void removePathCard(Card pathCard) {
        this.getChildren().remove(pathCard);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void showOptions(ArrayList<Language> languageList, String currentLanguage, Stage gameStage) {
        view.showOptions(languageList, currentLanguage, gameStage);
    }
}