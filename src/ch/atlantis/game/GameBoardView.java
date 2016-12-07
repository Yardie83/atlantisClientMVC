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
        Card cardToMoveToFront = null;
        for (Card card : gameModel.getPathCards()) {
            for (Tile tile : gameModel.getTiles()) {
                if (card.getCardType() == CardType.START && tile.getPathId() == 300) {
                    drawSpecialCards(card, tile);
                    card.setLayoutX(tile.getX() - tile.getSide());
                    card.setLayoutY(tile.getY());
                } else if (card.getCardType() == CardType.END && tile.getPathId() == 400) {
                    drawSpecialCards(card, tile);
                    card.setLayoutX(tile.getX() - tile.getSide() + 17);
                    card.setLayoutY(tile.getY() - tile.getSide());
                } else if (card.getPathId() == tile.getPathId()) {
                    drawMainPath(card, tile);
                    // In order to have the last pathCard before the end be drawn on top of the end
                    // we have to single out the last card and tell it specifically to be on top.
                    // Could not get it to work any other way.
                    // Another idea would be to single out the start and the end card and after we draw
                    // the path to draw the start and end and send them to the back. There is no problem with the first
                    // card for some reason
                    if(card.getPathId() == 153){
                        cardToMoveToFront = card;
                    }
                }
            }
            card.applyCardImages(listCardImages);
            this.getChildren().add(card);
        }
        // Here we tell the last path card to be on top in order to be drawn on top of the end card
        int index = gameModel.getPathCards().indexOf(cardToMoveToFront);
        gameModel.getPathCards().get(index).toFront();
    }

    private void drawMainPath(Card card, Tile tile) {
        card.setWidth(tile.getSide());
        card.setHeight(tile.getSide());
        card.setLayoutX(tile.getX());
        card.setLayoutY(tile.getY());
    }

    private void drawSpecialCards(Card card, Tile tile) {
        card.setWidth(tile.getSide()*3);
        card.setHeight(tile.getSide() * 2);
        card.toBack();
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
                    gamePiece.setLayoutX(startCard.getLayoutX() + offsetX + startCard.getWidth() / 3);
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
        localPlayerBox.setMinWidth(consoleTile.getSide() * 3);
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        HBoxMovementCards = placeMovementCards();
        localPlayerBox.getChildren().addAll(top, HBoxMovementCards);

        String score = Integer.toString(gameModel.getLocalPlayer().getScore());
        Label lblLocalPlayer = new Label(gameModel.getLocalPlayer().getPlayerName());
        lblScoreLocalPlayer = new Label("Score: " + score);
        Label label3 = new Label("|");

        top.getChildren().addAll(lblLocalPlayer, label3, lblScoreLocalPlayer);
        return localPlayerBox;
    }

    private VBox createOpponentBox() {
        VBox opponentsBox = new VBox(10);
        opponentsBox.setMinHeight(200);
        opponentsBox.setMinWidth(consoleTile.getSide() * 2);
        scoresLabels = new HashMap<>();
        for (Player player : gameModel.getPlayers()) {
            if (player.getPlayerID() != gameModel.getLocalPlayer().getPlayerID()) {
                Label lblOpponentName = new Label(player.getPlayerName());
                Label lblOpponentScore = new Label(Integer.toString(player.getScore()));
                scoresLabels.put(player.getPlayerID(), lblOpponentScore);
                opponentsBox.getChildren().addAll(lblOpponentName, lblOpponentScore);
            }
        }
        return opponentsBox;
    }

    private HBox placeMovementCards() {
        HBox bottom = new HBox(10);
        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayer().getPlayerID()).getMovementCards()) {
            System.out.println("GameBoard -> Card: " + card.getColorSet() + " added");
            styleMovementCard(card);
            bottom.getChildren().add(card);
        }
        return bottom;
    }

    private void styleMovementCard(Card card) {
        card.setWidth(60);
        card.setHeight(80);
        card.setStroke(Color.TRANSPARENT);
        card.setStrokeWidth(2);
        card.applyCardImages(listCardImages);
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
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {

                        //without the substring(4) the path is invalid resp nullPointerException
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
                if (gameModel.getLocalPlayer().getPlayerID() == gameModel.getPreviousTurn()) {
                    lblScoreLocalPlayer.setText("Score: " + String.valueOf(gameModel.getPlayers().get(gameModel.getPreviousTurn()).getScore()));
                } else {
                    scoresLabels.get(gameModel.getPreviousTurn()).setText("Score: " + String.valueOf(gameModel.getPlayers().get(gameModel.getPreviousTurn()).getScore()));
                }

                //Update the movementCards
                if (gameModel.getLocalPlayer().getPlayerID() == gameModel.getPreviousTurn()) {
                    HBoxMovementCards.getChildren().remove(gameModel.getCardPlayedIndex());
                    styleMovementCard(gameModel.getNewCardFromDeck());
                    HBoxMovementCards.getChildren().add(gameModel.getNewCardFromDeck());
                }

                //Remove the pathCards
                if(gamePieceToMove.getCurrentPathId()!= 101) {
                    Card pathCardToRemove = gameModel.getPathCards().get(gameModel.getIndexOfPathCardToRemove());
                    removePathCard(pathCardToRemove);
                }
            }
        });
    }

    public void removePathCard(Card pathCard) {
        this.getChildren().remove(pathCard);
    }

    public void showOptions(ArrayList<Language> languageList, String currentLanguage, Stage gameStage) {
        view.showOptions();
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