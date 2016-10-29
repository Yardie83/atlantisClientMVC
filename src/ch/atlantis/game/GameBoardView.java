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

    private GameModel gameModel;
    private int rowCount = 11;
    private Hand hand;
    private Deck deck;
    private AtlantisView view;
    private ArrayList<Tile> tiles;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCardsSetA;
    private ArrayList<Card> pathCardsSetB;
    private ArrayList<Card> movementCards;
    private ArrayList<Card> pathCards;
    private Stage gameStage;
    private Scene gameScene;

    public GameBoardView(GameModel gameModel, AtlantisView view) {

        this.gameModel = gameModel;
        this.view = view;

        int height = view.heightProperty().getValue();
        int width = view.widthProperty().getValue();

        super.setMinHeight(height);
        super.setMinWidth(width);

//        //TODO: Put background into css stylesheet and fix position
//        super.setBackground(new Background(new BackgroundImage(
//                new Image("ch/atlantis/res/gameboardbg.jpg"),
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundPosition.CENTER,
//                BackgroundSize.DEFAULT)));

        readLayout(height);
        //Currently Empty
        addPlayers();

        this.pathCardsSetA = new ArrayList<>();
        createPathCards(pathCardsSetA);
        cleanCardSetA(pathCardsSetA);
        Collections.shuffle(pathCardsSetA);

        this.pathCardsSetB = new ArrayList<>();
        createPathCards(pathCardsSetB);
        cleanCardSetB(pathCardsSetB);
        Collections.shuffle(pathCardsSetB);

        this.movementCards = new ArrayList<>();
        createMovementCards(movementCards);
        Collections.shuffle(movementCards);

        addHandCards(movementCards);

        addMovementCardsToDeck(movementCards);

        drawHand();

        drawBoard();

        drawGamePieces();
    }

    private void addMovementCardsToDeck(ArrayList<Card> movementCards) {
        this.deck = new Deck();

        deck.fillDeckToStart(movementCards);

    }

    private void drawHand() {
    }


    private void addPlayers() {
    }

    private void addHandCards(ArrayList<Card> movementCards) {
        if (players.size() == 2) {
            for (int i = 0; i < 2; i++) {
                Player player = players.get(i);
                if (i == 0) {
                    for (int k = 0; k < 4; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
                if (i == 1) {
                    for (int k = 0; k < 5; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
            }
        }
        if (players.size() == 3) {
            for (int i = 0; i < 3; i++) {
                Player player = players.get(i);
                if (i == 0) {
                    for (int k = 0; k < 4; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
                if (i == 1) {
                    for (int k = 0; k < 5; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
                if (i == 2) {
                    for (int k = 0; k < 6; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
            }
        }
        if (players.size() == 4) {
            for (int i = 0; i < 4; i++) {
                Player player = players.get(i);
                if (i == 0) {
                    for (int k = 0; k < 4; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
                if (i == 1) {
                    for (int k = 0; k < 5; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
                if (i == 2) {
                    for (int k = 0; k < 6; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
                if (i == 3) {
                    for (int k = 0; k < 7; k++) {
                        player.addHandCard(movementCards.get(k));
                        movementCards.remove(k);
                    }
                }
            }
        }
    }



    /**
     * Hermann Grieder
     * <p>
     * Creates the 105 movement cards and adds it to the movementCards ArrayList
     * @param movementCards
     */
    private void createMovementCards(ArrayList<Card> movementCards) {
        this.movementCards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 15; j++) {
                this.movementCards.add(new Card(i, CardType.MOVEMENT));
            }
        }
    }

    /**
     * Creates and adds 49 path cards to the card set
     * <p>
     * Author: Hermann Grieder
     *
     * @param pathCardsSet The ArrayList to store the cards in
     */
    private void createPathCards(ArrayList<Card> pathCardsSet) {
        for (int j = 0; j < 7; j++) {
            for (int k = 1; k <= 7; k++) {
                pathCardsSet.add(new Card(j, k, CardType.PATH));
            }
        }
    }

    /**
     * Removes the unneeded cards from the pathCardSetA List
     * The cards to be removed in A are different than in set B
     * <p>
     * <p>
     * Author: Fabian Witschi
     *
     * @param pathCardsSetA The first path card set
     */
    private void cleanCardSetA(ArrayList<Card> pathCardsSetA) {

        for (int i = 0; i < pathCardsSetA.size(); i++) {
            Card card = pathCardsSetA.get(i);
            int value = card.getValue();
            int colorSet = card.getColorSet();
            int index;
            if (value == 7) {
                if (colorSet == Card.GREY || colorSet == Card.YELLOW || colorSet == Card.BLUE || colorSet == Card.WHITE) {
                    index = pathCardsSetA.indexOf(card);
                    pathCardsSetA.remove(index);
                }
            } else if (value == 6) {
                if (colorSet == Card.BROWN || colorSet == Card.PINK || colorSet == Card.GREEN) {
                    index = pathCardsSetA.indexOf(card);
                    pathCardsSetA.remove(index);
                }
            }
        }
    }

    /**
     * Removes the unneeded cards from the pathCardSetB List.
     * The cards to be removed in B are different than in set A
     * <p>
     * Fabian Witschi
     *
     * @param pathCardsSetB The second path card set
     */
    private void cleanCardSetB(ArrayList<Card> pathCardsSetB) {

        for (int i = 0; i < pathCardsSetB.size(); i++) {
            Card card = pathCardsSetB.get(i);
            int value = card.getValue();
            int colorSet = card.getColorSet();
            int index;
            if (value == 7) {
                if (colorSet == Card.BROWN || colorSet == Card.PINK || colorSet == Card.GREEN) {
                    index = pathCardsSetB.indexOf(card);
                    pathCardsSetB.remove(index);
                }
            } else if (value == 6) {
                if (colorSet == Card.GREY || colorSet == Card.YELLOW || colorSet == Card.BLUE || colorSet == Card.WHITE) {
                    index = pathCardsSetB.indexOf(card);
                    pathCardsSetB.remove(index);
                }
            }
        }
    }

    /**
     * Adds 3 GamePieces to each Player in the players list
     * <p>
     * Author: Hermann Grieder
     */

    /**
     * Reads the GameBoardLayout.txt file and transfers the values into the values array
     * <p>
     * Author: Hermann Grieder
     *
     * @param height Height of the stage
     */
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

                        if (value != 000) {
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

    /**
     * Draws the GameBoard by placing the cards on the corresponding tile
     * <p>
     * Hermann Grieder
     */
    private void drawBoard() {

        Iterator<Card> iteratorA = pathCardsSetA.iterator();
        Iterator<Card> iteratorB = pathCardsSetB.iterator();

        pathCards = new ArrayList<>();

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
        deckBox.setMinWidth(tile.getSide() * 2);

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

        pathCards.add(card);

        card.setPathId(tile.getPathId());
        card.setWidth(tile.getSide());
        card.setHeight(tile.getSide());
        card.setLayoutX(tile.getX());
        card.setLayoutY(tile.getY());
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

        Player player = players.get(0);
        String score = Integer.toString(player.getScore());
        Label label1 = new Label(player.getPlayerName().toString());
        Label label2 = new Label("Score: " + score);
        Label label3 = new Label("|");

        top.getChildren().addAll(label1, label3, label2);

        int handCardSize = player.getHandCardSize();

        switch(handCardSize) {
            case 4:
                for (int i = 0; i < handCardSize; i++) {
                    Card handCard = player.getHandCard(i);
                    if (!(handCard.isPlayed())) {
                        bottom.getChildren().addAll(player.getHandCard(i));
                    } else {
                        handCard.setIsPlayedFalse();
                        deck.collectCard(handCard);
                        player.removeHandCard(handCard);
                    }
                }
                break;
            case 5:
                for (int i = 0; i < handCardSize; i++) {
                    bottom.getChildren().addAll(player.getHandCard(i));
                }
                break;
            case 6:
                for (int i = 0; i < handCardSize; i++) {
                    bottom.getChildren().addAll(player.getHandCard(i));
                }
                break;
            case 7:
                for (int i = 0; i < handCardSize; i++) {
                    bottom.getChildren().addAll(player.getHandCard(i));
                }
                break;
        }

        console.setLayoutX(tile.getX());
        console.setLayoutY(tile.getY() + 20);
        console.setMinHeight(200);
        console.setMinWidth(tile.getSide() * 7);

        this.getChildren().add(console);
    }

    public void show() {
        gameScene = new Scene(this);
        gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(gameScene);
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
    }

    public Stage getGameStage() { return gameStage; }

    public void removePathCard(Card pathCard) {
        this.getChildren().remove(pathCard);
    }

    public ArrayList<Player> getPlayers() { return players; }

    public int getPlayerId() { return getPlayerId(); }

    public void showOptions( ArrayList<Language> languageList, String currentLanguage, Stage gameStage ) {
        view.showOptions(languageList, currentLanguage, gameStage);
    }
}