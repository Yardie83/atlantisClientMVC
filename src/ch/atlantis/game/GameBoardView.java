package ch.atlantis.game;

import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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
    private Label lblScoreLocalPlayer;
    private HBox HBoxMovementCards;
    private Label infoLabel;
    private Button buttonGameOver;
    private GameOverView gameOverView;
    private Stage gameOverStage;
    private VBox stackCardPane;

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

        listCardImages = readCardImages();

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
                    if (card.getPathId() == 153) {
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
        card.setWidth(tile.getSide() * 3);
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
                // Style them
                styleGamePiece(player, gamePiece);
                // Add them to the view
                this.getChildren().add(gamePiece);
                offsetX += 20;
            }
            offsetY += 15;
            offsetX = 10;
        }
    }

    /**
     * Styles the gamePiece. Sets the color, width and height.
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
        stackCardPane = createPathCardsPane();
        ScrollPane scrollPane = new ScrollPane(stackCardPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMinViewportHeight(50);
        scrollPane.setVmax(50);
        scrollPane.setPrefSize(100,50);
        scrollPane.setMaxHeight(150);
        VBox otherPlayersBox = createOpponentBox();
        VBox localPlayerBox = createLocalPlayerBox();
        VBox gameControls = createGameControls();

        console.getChildren().addAll(scrollPane, otherPlayersBox, localPlayerBox, gameControls);

        this.getChildren().add(console);

    }

    private VBox createPathCardsPane() {
        VBox pathCardsPane = new VBox(3);
        pathCardsPane.setMaxWidth(50);
        pathCardsPane.setMinWidth(50);
        pathCardsPane.setMinHeight(50);
        return pathCardsPane;
    }

    private HBox initConsole() {
        HBox console = new HBox();
        console.setLayoutX(consoleTile.getX());
        console.setLayoutY(consoleTile.getY() + 20);
        console.setMinHeight(150);
        console.setMinWidth(consoleTile.getSide() * 9);
        return console;
    }

    private VBox createGameControls() {
        VBox gameControls = new VBox(10);

        buttonBuyCards = new Button("Buy Cards");
        buttonMove = new Button("Move");
        buttonReset = new Button("Reset");
        buttonReset.setDisable(true);
        buttonEndTurn = new Button("End Turn");
        buttonEndTurn.setDisable(true);
        buttonGameOver = new Button("Game Over Test");

        gameControls.getChildren().addAll(buttonBuyCards, buttonMove, buttonReset, buttonEndTurn, buttonGameOver);

        return gameControls;
    }

    private VBox createLocalPlayerBox() {
        VBox localPlayerBox = new VBox(10);
        localPlayerBox.setMinHeight(150);
        localPlayerBox.setMinWidth(consoleTile.getSide() * 4);
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        HBoxMovementCards = placeMovementCards();
        infoLabel = new Label("");
        infoLabel.setStyle("-fx-text-fill: white");
        localPlayerBox.getChildren().addAll(top, HBoxMovementCards, infoLabel);

        String score = Integer.toString(gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getScore());
        Label lblLocalPlayer = new Label(gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPlayerName());
        lblLocalPlayer.setStyle("-fx-text-fill: white");
        lblScoreLocalPlayer = new Label("Score: " + score);
        lblScoreLocalPlayer.setStyle("-fx-text-fill: white");
        Label label3 = new Label("|");
        label3.setStyle("-fx-text-fill: white");

        top.getChildren().addAll(lblLocalPlayer, label3, lblScoreLocalPlayer);
        return localPlayerBox;
    }

    private VBox createOpponentBox() {
        VBox opponentsBox = new VBox(10);
        opponentsBox.setMinHeight(200);
        opponentsBox.setMinWidth(consoleTile.getSide());
        scoresLabels = new HashMap<>();
        for (Player player : gameModel.getPlayers()) {
            if (player.getPlayerID() != gameModel.getLocalPlayerId()) {
                Label lblOpponentName = new Label(player.getPlayerName());
                lblOpponentName.setStyle("-fx-text-fill: white");
                Label lblOpponentScore = new Label(Integer.toString(player.getScore()));
                lblOpponentScore.setStyle("-fx-text-fill: white");
                scoresLabels.put(player.getPlayerID(), lblOpponentScore);
                opponentsBox.getChildren().addAll(lblOpponentName, lblOpponentScore);
            }
        }
        return opponentsBox;
    }

    private HBox placeMovementCards() {
        HBox bottom = new HBox(10);
        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()) {
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
        card.setOpacity(1);
        card.setDisable(false);
        card.applyCardImages(listCardImages);
    }

    /**
     * Loris Grether
     *
     * @return
     */
    private Hashtable<String, ImageView> readCardImages() {
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
        setInfoLblTextOnNewTurn();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameStage.setScene(gameScene);
            }
        });

    }

    private void setInfoLblTextOnNewTurn() {
        if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()){
            setInfoLabelText("Your turn\nSelect a game piece and a card");
        }else{
            setInfoLabelText(gameModel.getPlayers().get(gameModel.getCurrentTurn()).getPlayerName() +"'s turn. Please wait.");
        }
    }


    //*************************** METHODS DURING THE ACTIVE GAME *****************************//

    public void moveGamePiece() {
        GamePiece selectedGamePiece = gameModel.getSelectedGamePiece();
        int targetPathId = selectedGamePiece.getCurrentPathId();
        move(selectedGamePiece, targetPathId);
    }

    public void moveGamePiece(GamePiece selectedGamePiece) {
        int targetPathId = selectedGamePiece.getCurrentPathId();
        move(selectedGamePiece, targetPathId);
    }

    private void move(GamePiece selectedGamePiece, int targetPathId) {
        for (Tile targetTile : gameModel.getTiles()) {
            if (targetTile.getPathId() == targetPathId) {
                int x = targetTile.getX() + (targetTile.getSide() / 2);
                int y = targetTile.getY() + (targetTile.getSide() / 2);
                selectedGamePiece.move(x, y);
            }
        }
    }

    public void updateBoard() {

        GamePiece selectedGamePiece = gameModel.getPlayers().get(gameModel.getPreviousTurn()).getGamePieces().get(gameModel.getGamePieceUsedIndex());
        ArrayList<Card> cards = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards();
        int previousTurn = gameModel.getPreviousTurn();
        int score = gameModel.getPlayers().get(previousTurn).getScore();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //Move the gamePiece
                moveGamePiece(selectedGamePiece);

                //Update the score and the infoLabel and
                //add the pathCard that was picked up to the flowPane.
                if (gameModel.getLocalPlayerId() == previousTurn) {
                    updateScoreLabel(lblScoreLocalPlayer, score);
                    setInfoLblTextOnNewTurn();
                    updatePathCardsStack();
                } else {
                    updateScoreLabel(scoresLabels.get(previousTurn), score);
                    setInfoLblTextOnNewTurn();
                }

                //Update the movementCards
                if (gameModel.getLocalPlayerId() == previousTurn) {
                    for (Card card : cards) {
                        styleMovementCard(card);
                        HBoxMovementCards.getChildren().removeAll(card);
                    }
                }
                HBoxMovementCards.getChildren().setAll(gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards());

                //Remove the pathCards
                if (selectedGamePiece.getCurrentPathId() != 101) {
                    Card pathCardToRemove = gameModel.getPathCards().get(gameModel.getIndexOfPathCardToRemove());
                    removePathCard(pathCardToRemove);
                }
            }
        });
    }

    private void updatePathCardsStack() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stackCardPane.getChildren().setAll(gameModel.getPlayers().get(gameModel.getPreviousTurn()).getPathCardStack());
            }
        });
    }

    private void updateScoreLabel(Label scoreLabel, int score) {
        System.out.println("Total of " + score + " points");
        System.out.println("CurrentTurn: " + gameModel.getCurrentTurn());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                scoreLabel.setText("Score: " + String.valueOf(score));
            }
        });
    }

    public void setDisableButtonMove(boolean disableButtonMove) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buttonMove.setDisable(disableButtonMove);
            }
        });
    }

    public void setDisableButtonEndTurn(boolean disableButtonEndTurn) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buttonEndTurn.setDisable(disableButtonEndTurn);
            }
        });
    }

    public void setInfoLabelText(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                infoLabel.setText(s);
                System.out.println(s);
            }
        });
    }

    public void resetCards() {
        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()){
            card.setOpacity(1);
            card.setDisable(false);
            resetHighlight(card);
        }
    }

    public void createGameOverView() {
        Stage parentStage = gameStage;
        if (this.gameOverView == null) {
            this.gameOverView = new GameOverView(gameStage.getHeight(), gameStage.getWidth());
            gameOverStage = new Stage();
            gameOverStage.setScene(new Scene(gameOverView));
            view.setupOverlay(gameOverStage, parentStage, "css_GameOverView");
        }
        view.setXYLocation(gameOverStage, parentStage);
        view.setDimensions(gameOverStage, parentStage);

        //getControls(this.createGameView);
        //setControlText(this.controls);

        //activeOverlayStage = createGameStage;
    }

    public void showGameOver() {
        gameOverStage.show();
    }

    public void removePathCard(Card pathCard) {
        this.getChildren().remove(pathCard);
    }

    public void showOptions() {
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

    public Button getButtonGameOver() {
        return buttonGameOver;
    }

    public GameOverView getGameOverView() {
        return gameOverView;
    }

}