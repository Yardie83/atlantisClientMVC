package ch.atlantis.game;

import ch.atlantis.util.Language;
import ch.atlantis.view.AtlantisView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    public GameBoardView(GameModel gameModel, AtlantisView view) {

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
                    card.applyColor();
                    this.getChildren().add(card);
                }
            }
        }
    }

    private void drawGamePieces() {
        double offsetX = 10;
        double offsetY =  5;
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

        this.scoresLabels = new HashMap<>();

        HBox console = new HBox();
        VBox otherPlayersBox = new VBox(10);
        otherPlayersBox.setMinHeight(200);
        otherPlayersBox.setMinWidth(consoleTile.getSide() * 2);

        for (Player player : players) {
            if (!player.getPlayerName().equals(localPlayer.getGameName())) {
                Label labelName = new Label(player.getPlayerName());
                Label labelScore = new Label(Integer.toString(player.getScore()));
                scoresLabels.put(player.getPlayerID(), labelScore);
                otherPlayersBox.getChildren().addAll(labelName, labelScore);
            }
        }

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

        console.setLayoutX(consoleTile.getX());
        console.setLayoutY(consoleTile.getY() + 20);
        console.setMinHeight(200);
        console.setMinWidth(consoleTile.getSide() * 9);

       for(Card card : localPlayer.getMovementCards()){
           card.setWidth(60);
           card.setHeight(80);
           card.applyColor();
           localPlayerBox.getChildren().add(card);
       }

        console.getChildren().addAll(otherPlayersBox, localPlayerBox);

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

    public HashMap<Integer, Label> getLabels() { return scoresLabels; }

}