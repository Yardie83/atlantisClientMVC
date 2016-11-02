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
        pathCards = gameModel.getPathCards();
        deck = gameModel.getDeck();

        drawHand();

        drawCards(pathCards, tiles);

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

    private void drawCards(ArrayList<Card> pathCards, ArrayList<Tile> tiles) {

        for (Card card : pathCards) {
            for (Tile tile : tiles) {
                if (card.getPathId() == tile.getPathId()) {
                    card.setWidth(tile.getSide());
                    card.setHeight(tile.getSide());
                    card.setLayoutX(tile.getX());
                    card.setLayoutY(tile.getY());
                    card.applyColor();
                    this.getChildren().add(card);
                }
            }
        }
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