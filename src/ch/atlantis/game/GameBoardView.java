package ch.atlantis.game;

import ch.atlantis.util.Language;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Hermann Grieder on 23.08.2016.
 * <p>
 * The gameBoard that will be shown on game start.
 */
public class GameBoardView extends Pane {

    private HashMap<Integer, Label> scoresLabels;
    private GameModel gameModel;
    private AtlantisView view;
    private Tile consoleTile;
    private Stage gameStage;

    private Button buttonBuyCards;
    private Button buttonMove;
    private Button buttonReset;
    private Button buttonEndTurn;
    private Hashtable<String, ImageView> listCardImages;
    private Label lblLocalPlayer;
    private Label lblScoreLocalPlayer;
    private HBox HBoxMovementCards;

    public GameBoardView(GameModel gameModel, AtlantisView view) {

        this.gameModel = gameModel;
        this.view = view;

        initBoard();
    }

    /**
     * Sets the X and Y values for each tile calculated by the screen height of individual users. Draws the cards,
     * the gamePieces and the player console.
     */
    private void initBoard() {

        int height = view.heightProperty().getValue();
        int width = view.widthProperty().getValue();
        super.setMinHeight(height);
        super.setMinWidth(width);

        int rowCount = 11;
        int side = (height / rowCount);

        for (Tile tile : gameModel.getTiles()) {
            tile.setX(tile.getX() * side);
            tile.setY(tile.getY() * side);
            tile.setSide(side);
        }

        listCardImages = readCards();

        drawPath();

        drawGamePieces();

        drawConsole();
    }

    private void drawPath() {

        for (Card card : gameModel.getPathCards()) {
            for (Tile tile : gameModel.getTiles()) {
                if (card.getPathId() == tile.getPathId()) {
                    card.setWidth(tile.getSide());
                    card.setHeight(tile.getSide());
                    card.setLayoutX(tile.getX());
                    card.setLayoutY(tile.getY());
                    card.applyCardImages(listCardImages);
                    this.getChildren().add(card);
                }
            }
        }
    }

    private void drawGamePieces() {
        // First find the start of the game to place the gamePieces onto
        Card startCard = null;
        for (Card card : gameModel.getPathCards()) {
            if (card.getCardType() == CardType.START) {
                startCard = card;
            }
        }

        // Offset values so the gamePieces are not placed on top of each other upon start
        int offsetX = 10;
        int offsetY = 5;
        // For each player place each gamePiece onto the start card
        for (Player player : gameModel.getPlayers()) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                if (startCard != null) {
                    gamePiece.setLayoutX(startCard.getLayoutX() + offsetX);
                    gamePiece.setLayoutY(startCard.getLayoutY() + offsetY);
                    gamePiece.setCurrentPathId(startCard.getPathId());
                }
                styleGamePiece(player, gamePiece);

                this.getChildren().add(gamePiece);
                offsetX += 20;
            }
            offsetY += 15;
            offsetX = 10;
        }
    }

    /**
     * Styles the gamePiece. Sets the color, width and height as well as the style class for CSS
     *
     * @param player    The player to whom the gamePiece belongs to
     * @param gamePiece The gamePiece to be styled
     */
    private void styleGamePiece(Player player, GamePiece gamePiece) {
        gamePiece.setWidth(10);
        gamePiece.setHeight(10);
        gamePiece.setStroke(Color.TRANSPARENT);
        gamePiece.setStrokeWidth(2);
        gamePiece.setFill(player.getColor());
    }

    private void drawConsole() {
        for (Tile tile : gameModel.getTiles())
            if (tile.getPathId() == 500) {
                consoleTile = tile;
            }

        HBox console = initConsole();
        VBox otherPlayersBox = createOpponentBox();
        VBox localPlayerBox = createLocalPlayerBox();
        VBox gameControls = createGameControls();

        console.getChildren().addAll(otherPlayersBox, localPlayerBox, gameControls);

        this.getChildren().add(console);

    }

    private HBox initConsole() {
        HBox console = new HBox();
        console.setLayoutX(consoleTile.getX());
        console.setLayoutY(consoleTile.getY() + 20);
        console.setMinHeight(200);
        console.setMinWidth(consoleTile.getSide() * 9);
        return console;
    }

    private VBox createGameControls() {
        VBox gameControls = new VBox(10);

        buttonBuyCards = new Button("Buy Cards");
        buttonMove = new Button("Move");
        buttonReset = new Button("Reset");
        buttonEndTurn = new Button("End Turn");

        gameControls.getChildren().addAll(buttonBuyCards, buttonMove, buttonReset, buttonEndTurn);

        return gameControls;
    }

    private VBox createLocalPlayerBox() {
        VBox localPlayerBox = new VBox(10);
        localPlayerBox.setMinHeight(200);
        localPlayerBox.setMinWidth(consoleTile.getSide() * 7);
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        HBoxMovementCards = placeMovementCards();
        localPlayerBox.getChildren().addAll(top, HBoxMovementCards);

        localPlayerBox.setStyle("-fx-border-width: 1px; " +
                "-fx-background-color: #7af5c4;" +
                "-fx-border-color: black");


        String score = Integer.toString(gameModel.getLocalPlayer().getScore());
        Label lblLocalPlayer = new Label(gameModel.getLocalPlayer().getPlayerName());
        lblScoreLocalPlayer = new Label("Score: " + score);
        Label label3 = new Label("|");

        top.getChildren().addAll(lblLocalPlayer, label3, lblScoreLocalPlayer);
        return localPlayerBox;
    }

    private VBox createOpponentBox() {
        VBox otherPlayersBox = new VBox(10);
        otherPlayersBox.setMinHeight(200);
        otherPlayersBox.setMinWidth(consoleTile.getSide() * 2);
        scoresLabels = new HashMap<>();
        for (Player player : gameModel.getPlayers()) {
            if (!player.getPlayerName().equals(gameModel.getLocalPlayer().getGameName())) {
                Label lblOpponentName = new Label(player.getPlayerName());
                Label lblOpponentScore = new Label(Integer.toString(player.getScore()));
                scoresLabels.put(player.getPlayerID(), lblOpponentScore);
                otherPlayersBox.getChildren().addAll(lblOpponentName, lblOpponentScore);
            }
        }
        return otherPlayersBox;
    }

    private HBox placeMovementCards() {
        HBox bottom = new HBox(10);
        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayer().getPlayerID()).getMovementCards()) {
            card.setWidth(60);
            card.setHeight(80);
            card.setStroke(Color.TRANSPARENT);
            card.setStrokeWidth(2);
            card.applyCardImages(listCardImages);
            bottom.getChildren().add(card);
        }
        return bottom;
    }

    private Hashtable<String, ImageView> readCards() {
        Hashtable<String, ImageView> listCardImages = new Hashtable<>();

        File folder = new File("src/ch/atlantis/res/Spielmaterial/");

        if (!folder.isDirectory()) {
            //TODO:Error blabla
        }

        File[] myFiles = folder.listFiles();

        if (myFiles != null) {
            for (File file : myFiles) {
                if (file.exists() && file.isFile()) {
                    if (file.getName().endsWith(".jpg")) {

                        //without the substring(4) the path is invalid resp nullpointerexception
                        ImageView imageView = new ImageView(new Image(file.getPath().substring(4)));
                        listCardImages.put(file.getName(), imageView);
                    }
                }
            }
        }
        return listCardImages;
    }

    public void show() {

        String css = this.getClass().getResource("../res/css/css_Game.css").toExternalForm();
        Scene gameScene = new Scene(this);
        gameScene.getStylesheets().add(css);
        gameStage = view.getGameLobbyView().getGameLobbyStage();
        gameStage.setScene(gameScene);
    }


    //*************************** METHODS DURING THE ACTIVE GAME *****************************//

    public void moveGamePiece(int targetPathId, GamePiece selectedGamePiece) {

        for (Tile targetTile : gameModel.getTiles()) {
            if (targetTile.getPathId() == targetPathId) {
                int x = targetTile.getX() + (targetTile.getSide() / 2);
                int y = targetTile.getY() + (targetTile.getSide() / 2);
                selectedGamePiece.move(x, y);
            }
        }
    }

    public void updateBoard() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //Move the gamePiece
                GamePiece gamePieceToMove = gameModel.getGamePieceToMove();
                int targetPathId = gamePieceToMove.getCurrentPathId();
                System.out.println("TargetPathId: " + targetPathId);
                moveGamePiece(targetPathId, gamePieceToMove);

                //Update the score
                if (gameModel.getLocalPlayer().getPlayerID() == gameModel.getPreviousTurn()){
                    lblScoreLocalPlayer.setText("Score: " + String.valueOf(gameModel.getPlayers().get(gameModel.getPreviousTurn()).getScore()));
                }else{
                    scoresLabels.get(gameModel.getPreviousTurn()).setText("Score: " + String.valueOf(gameModel.getPlayers().get(gameModel.getPreviousTurn()).getScore()));
                }

//                //Update the movementCards
//                if (gameModel.getLocalPlayer().getPlayerID() == gameModel.getPreviousTurn()) {
//                    HBoxMovementCards = placeMovementCards();
//                }

                //Remove the pathCards
                Card pathCardToRemove = gameModel.getPathCards().get(gameModel.getIndexOfPathCardToRemove());
                removePathCard(pathCardToRemove);
            }
        });

    }

    public void removePathCard(Card pathCard) {
        this.getChildren().remove(pathCard);
    }

    public void showOptions(ArrayList<Language> languageList, String currentLanguage, Stage gameStage) {
        view.showOptions(languageList, currentLanguage, gameStage);
    }

    // ************************************* Styling Methods & CSS ***************************************** //

    void resetHighlight(Rectangle item) {
        item.setStroke(Color.TRANSPARENT);
        item.setStrokeWidth(2);
    }

    void highlightItem(Rectangle item) {
        item.setStroke(Color.BLACK);
        item.setStrokeWidth(2);
    }


    // ************************************* GETTERS / SETTERS ********************************************* //

    public Stage getGameStage() {
        return gameStage;
    }

    public HashMap<Integer, Label> getLabels() {
        return scoresLabels;
    }

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