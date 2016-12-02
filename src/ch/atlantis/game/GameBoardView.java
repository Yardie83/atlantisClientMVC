package ch.atlantis.game;

import ch.atlantis.util.Language;
import ch.atlantis.view.AtlantisView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hermann Grieder on 23.08.2016.
 * <p>
 * The gameBoard that will be shown on game start.
 */
public class GameBoardView extends Pane {

    private AtlantisView view;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCards;
    private Stage gameStage;
    private int height;
    private Player localPlayer;
    private Tile consoleTile;
    private HashMap<Integer, Label> scoresLabels;
    private GameModel gameModel;
    private Button buttonBuyCards;
    private Button buttonMove;
    private Button buttonReset;
    private Button buttonEndTurn;

    public GameBoardView(GameModel gameModel, AtlantisView view) {

        this.gameModel = gameModel;

        this.view = view;

        height = view.heightProperty().getValue();
        int width = view.widthProperty().getValue();

        super.setMinHeight(height);
        super.setMinWidth(width);
        ArrayList<Tile> tiles = setXYTiles(gameModel.getTiles());
        localPlayer = gameModel.getLocalPlayer();
        players = gameModel.getPlayers();
        pathCards = gameModel.getPathCards();

        drawCards(pathCards, tiles);

        drawGamePieces();

        createGameConsole();
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

    private void drawCards(ArrayList<Card> pathCards, ArrayList<Tile> tiles) {

        for (Card card : pathCards) {
            for (Tile tile : tiles) {
                if (tile.getPathId() == 500) {
                    consoleTile = tile;
                }
                if (card.getPathId() == tile.getPathId()) {
                    card.setWidth(tile.getSide());
                    card.setHeight(tile.getSide());
                    card.setLayoutX(tile.getX());
                    card.setLayoutY(tile.getY());
                    card.setStroke(Color.BLACK);

                    addWater(card);

                    //addStartEndCard();

                    card.applyCardImages(gameModel.getListCardImages());
                    //TODO: At this place the card-image will be added to the card
                    this.getChildren().add(card);
                }
            }
        }
    }

    private void addWater(Card card) {

        card.setFill(new ImagePattern(gameModel.getListCardImages().get("water.jpg").getImage()));

    }

    private void drawGamePieces() {
        int offsetX = 10;
        int offsetY = 5;
        for (Card card : pathCards) {
            if (card.getCardType() == CardType.START) {
                for (Player player : players) {
                    for (GamePiece gamePiece : player.getGamePieces()) {
                        //Put the game pieces onto the start field
                        gamePiece.setLayoutX(card.getLayoutX() + offsetX);
                        gamePiece.setLayoutY(card.getLayoutY() + offsetY);
                        gamePiece.setPathId(card.getPathId());
                        gamePiece.setFill(player.getColor());
                        gamePiece.setStroke(Color.BLACK);
                        gamePiece.setWidth(10);
                        gamePiece.setHeight(10);
                        this.getChildren().add(gamePiece);
                        offsetX += 20;
                    }
                    offsetY += 15;
                    offsetX = 10;
                }
            }
        }
    }

    private void createGameConsole() {

        HBox console = initConsole();
        VBox otherPlayersBox = createOtherPlayersBox();
        HBox localPlayerBox = createLocalPlayerBox();
        VBox gameControls = createGameControls();
        placeMovementCards(localPlayerBox);

        console.getChildren().addAll(otherPlayersBox, localPlayerBox, gameControls);

        this.getChildren().add(console);
    }

    private VBox createGameControls() {
        VBox gameControls = new VBox(10);

        buttonBuyCards = new Button("Buy Cards");
        buttonMove = new Button("Move");
        buttonReset = new Button("Reset");
        buttonEndTurn = new Button("End Turn");

        gameControls.getChildren().addAll(buttonBuyCards, buttonMove, buttonReset, buttonEndTurn);

        return  gameControls;
    }

    private HBox initConsole() {
        HBox console = new HBox();
        console.setLayoutX(consoleTile.getX());
        console.setLayoutY(consoleTile.getY() + 20);
        console.setMinHeight(200);
        console.setMinWidth(consoleTile.getSide() * 9);
        return console;
    }

    private HBox createLocalPlayerBox() {
        HBox localPlayerBox = new HBox();
        localPlayerBox.setMinHeight(200);
        localPlayerBox.setMinWidth(consoleTile.getSide() * 7);
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        HBox bottom = new HBox(10);

        localPlayerBox.getChildren().addAll(top, bottom);

        localPlayerBox.setStyle("-fx-border-width: 1px; " +
                "-fx-background-color: #7af5c4;" +
                "-fx-border-color: black");


        String score = Integer.toString(localPlayer.getScore());
        Label label1 = new Label(localPlayer.getPlayerName());
        Label label2 = new Label("Score: " + score);
        Label label3 = new Label("|");

        top.getChildren().addAll(label1, label3, label2);
        return localPlayerBox;
    }

    private VBox createOtherPlayersBox() {
        VBox otherPlayersBox = new VBox(10);
        otherPlayersBox.setMinHeight(200);
        otherPlayersBox.setMinWidth(consoleTile.getSide() * 2);
        scoresLabels = new HashMap<>();
        for (Player player : players) {
            if (!player.getPlayerName().equals(localPlayer.getGameName())) {
                Label labelName = new Label(player.getPlayerName());
                Label labelScore = new Label(Integer.toString(player.getScore()));
                scoresLabels.put(player.getPlayerID(), labelScore);
                otherPlayersBox.getChildren().addAll(labelName, labelScore);
            }
        }
        return otherPlayersBox;
    }

    private void placeMovementCards(HBox localPlayerBox) {
        for(Card card : localPlayer.getMovementCards()){
            card.setWidth(60);
            card.setHeight(80);
            card.applyCardImages(gameModel.getListCardImages());
            localPlayerBox.getChildren().add(card);
        }
    }

    public void show() {

        String css = this.getClass().getResource("../res/css/css_Game.css").toExternalForm();
        Scene gameScene = new Scene(this);
        gameScene.getStylesheets().add(css);
        gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(gameScene);
    }


    //*************************** METHODS DURING THE ACTIVE GAME *****************************//

    public void moveGamePiece(int nextPathId, GamePiece selectedGamePiece) {
        System.out.println(nextPathId);
        for (Tile nextTile : gameModel.getTiles()){
            if (nextTile.getPathId() == nextPathId){
                int x = nextTile.getX();
                int y = nextTile.getY();
                selectedGamePiece.move(x,y);
            }
        }
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

    public HashMap<Integer, Label> getLabels() { return scoresLabels; }


    // ************************************* GETTERS / SETTERS ********************************************* //


    public Button getButtonBuyCards() {
        return buttonBuyCards;
    }

    public Button getButtonMove() {
        return buttonMove;
    }

    public Button getButtonReset() {
        return buttonReset;
    }

    public Button getButtonEndTurn() {
        return buttonEndTurn;
    }

}