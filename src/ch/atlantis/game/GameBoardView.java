package ch.atlantis.game;

import ch.atlantis.AtlantisClient;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Logger;

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
    private Button buttonGameRules;
    private Button buttonCantMove;
    private Hashtable<String, ImageView> listCardImages;
    private Label lblScoreLocalPlayer;
    private HBox HBoxMovementCards;
    private Label infoLabel;
    private Button buttonPay;
    private GameOverView gameOverView;
    private Stage gameOverStage;
    private VBox stackCardPane;
    private Label lblScoreText;
    private Label lblScoreNumber;

    private int offsetX = 10;
    private int offsetY = 5;

    private Logger logger;
    private int width;
    private HBox bottomHBox;
    private Label lblStatus;

    public GameBoardView(GameModel gameModel, AtlantisView view) {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        this.gameModel = gameModel;
        this.view = view;

        initBoard();
    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the X and Y values for each tile calculated by the screen height of individual users. Draws the cards,
     * the gamePieces and the player console.
     */
    private void initBoard() {

        int height = view.heightProperty().getValue();
        width = view.widthProperty().getValue();
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

        addInfoLabel();

        setCSSIds();
    }

    /**
     * Hermann Grieder
     * <br>
     * Draws the path
     */
    private void drawPath() {
        for (Card card : gameModel.getPathCards()) {
            for (Tile tile : gameModel.getTiles()) {
                if (card.getCardType() == CardType.START && tile.getPathId() == 300) {
                    drawSpecialCards(card, tile);
                    card.setLayoutX(tile.getX() - tile.getSide());
                    card.setLayoutY(tile.getY());
                    card.toBack();
                } else if (card.getCardType() == CardType.END && tile.getPathId() == 400) {
                    drawSpecialCards(card, tile);
                    card.setLayoutX(tile.getX() - tile.getSide() + 15);
                    card.setLayoutY(tile.getY() - (tile.getSide() / 3));
                    card.toBack();
                } else if (card.getPathId() == tile.getPathId()) {
                    drawMainPath(card, tile);
                    if (card.getPathId() == 153) {
                        int index = gameModel.getPathCards().indexOf(card);
                        gameModel.getPathCards().get(index).toFront();
                    }
                }
            }
            card.applyCardImages(listCardImages);
            this.getChildren().add(card);
        }

    }

    /**
     * Hermann Grieder
     * <br>
     * Draws the main path
     */
    private void drawMainPath(Card card, Tile tile) {
        card.setWidth(tile.getSide());
        card.setHeight(tile.getSide());
        card.setLayoutX(tile.getX());
        card.setLayoutY(tile.getY());
    }

    /**
     * Hermann Grieder
     * <br>
     * Draws the start and the end
     */
    private void drawSpecialCards(Card card, Tile tile) {
        card.setWidth(tile.getSide() * 3);
        card.setHeight(tile.getSide() * 2);
        card.toBack();
    }

    /**
     * Hermann Grieder
     * <br>
     * Draws the game piece on start of the game
     */
    private void drawGamePieces() {
        // First find the start of the game to place the gamePieces onto
        Card startCard = null;
        for (Card card : gameModel.getPathCards()) {
            if (card.getCardType() == CardType.START) {
                startCard = card;
            }
        }

        // Offset values so the gamePieces are not placed on top of each other upon start
        offsetX = 10;
        offsetY = 5;
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
     * Hermann Grieder
     * <br>
     * Styles the gamePiece. Sets their color, width and height.
     *
     * @param player    The player to whom the gamePiece belongs to
     * @param gamePiece The gamePiece to be styled
     */
    private void styleGamePiece(Player player, GamePiece gamePiece) {
        gamePiece.setRadius(10);

        gamePiece.setStroke(Color.TRANSPARENT);
        gamePiece.setStrokeWidth(2);
        gamePiece.setFill(player.getColor());
    }

    /**
     * Hermann Grieder
     * <br>
     * Draws the "console" in the bottom of the game board view. The console contains the path cards that where picked
     * up during the game, the opponents names and score, the player's movement card and his score, and all the game
     * control buttons.
     */
    private void drawConsole() {
        for (Tile tile : gameModel.getTiles())
            if (tile.getPathId() == 500) {
                consoleTile = tile;
            }

        HBox console = initConsole();
        stackCardPane = createPathCardsPane();
        ScrollPane scrollPaneStackCards = new ScrollPane(stackCardPane);
        scrollPaneStackCards.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneStackCards.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneStackCards.setMinViewportHeight(50);
        scrollPaneStackCards.setVmax(50);
        scrollPaneStackCards.setPrefSize(100, 50);
        scrollPaneStackCards.setMaxHeight(150);

        VBox otherPlayersBox = createOpponentBox();
        VBox localPlayerBox = createLocalPlayerBox();
        VBox gameControls = createGameControls();
        gameControls.setTranslateY(40);

        console.getChildren().addAll(scrollPaneStackCards, otherPlayersBox, localPlayerBox, gameControls);

        this.getChildren().add(console);

    }

    /**
     * Fabian Witschi
     *
     * @return VBox
     */
    private VBox createPathCardsPane() { // Create VBox with score cards
        VBox pathCardsPane = new VBox(3);
        pathCardsPane.setMaxWidth(50);
        pathCardsPane.setMinWidth(50);
        pathCardsPane.setMinHeight(50);
        return pathCardsPane;
    }

    /**
     * Hermann Grieder
     * <br>
     *
     * @return HBox
     */
    private HBox initConsole() {
        HBox console = new HBox(10);
        console.setLayoutX(consoleTile.getX());
        console.setLayoutY(consoleTile.getY() + 20);
        console.setMinHeight(150);
        console.setMinWidth(consoleTile.getSide() * 9);
        return console;
    }

    /**
     * Hermann Grieder
     * <br>
     *
     * @return VBox with all the game controls
     */
    private VBox createGameControls() {
        VBox gameControls = new VBox(10);
        HBox firstRow = new HBox(5);
        buttonMove = new Button("Move");
        buttonReset = new Button("Reset");
        buttonGameRules = new Button("Rules");
        buttonCantMove = new Button("Cant Move");
        firstRow.getChildren().addAll(buttonMove, buttonReset, buttonGameRules, buttonCantMove);
        buttonEndTurn = new Button("End Turn");
        HBox secondRow = new HBox(5);
        buttonBuyCards = new Button("Buy Cards");
        buttonPay = new Button("Pay to cross");
        secondRow.getChildren().addAll(buttonEndTurn, buttonBuyCards, buttonPay);

        buttonReset.setDisable(true);
        buttonPay.setDisable(true);
        buttonCantMove.setDisable(true);
        setDisableButtonEndTurn(true);
        buttonBuyCards.setDisable(true);
        if (gameModel.getCurrentTurn() != gameModel.getLocalPlayerId()) {
            setDisableButtonMove(true);
            buttonCantMove.setDisable(true);
        }

        gameControls.getChildren().addAll(firstRow, secondRow);

        return gameControls;
    }

    /**
     * Hermann Grieder
     * <br>
     * The local player's movement cards and score. The movement cards are inside a scrollPane, so when the
     * player has more than 4 cards he can scroll through the rest of his cards.
     *
     * @return VBox
     */
    private VBox createLocalPlayerBox() {
        VBox localPlayerBox = new VBox(10);
        localPlayerBox.setMinHeight(150);
        localPlayerBox.setMinWidth(consoleTile.getSide() * 4);
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);

        String score = "0";
        Label lblLocalPlayer = new Label(gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPlayerName());
        lblLocalPlayer.setStyle("-fx-text-fill: white");
        HBox scoreBox = new HBox();
        lblScoreText = new Label("Score: ");
        lblScoreNumber = new Label("0");
        lblScoreNumber.setStyle("-fx-text-fill: white");
        lblScoreText.setStyle("-fx-text-fill: white");
        lblScoreLocalPlayer = new Label(score);
        scoreBox.getChildren().addAll(lblScoreText, lblScoreNumber, lblScoreLocalPlayer);
        lblScoreLocalPlayer.setStyle("-fx-text-fill: white");
        Label label3 = new Label("|");
        label3.setStyle("-fx-text-fill: white");

        top.getChildren().addAll(lblLocalPlayer, label3, scoreBox);

        HBoxMovementCards = placeMovementCards();

        ScrollPane scrollPaneMovementCards = new ScrollPane(HBoxMovementCards);
        scrollPaneMovementCards.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneMovementCards.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneMovementCards.setMinViewportHeight(50);
        scrollPaneMovementCards.setPrefSize(50, 100);
        scrollPaneMovementCards.setMaxHeight(150);
        scrollPaneMovementCards.minViewportHeightProperty().set(50);

        localPlayerBox.getChildren().addAll(top, HBoxMovementCards, scrollPaneMovementCards);

        return localPlayerBox;
    }

    /**
     * Hermann Grieder
     * <br>
     * The infoLabel on top of the game. Informs the player of his color, the current turn and what needs to be done
     * during moves.
     */
    private void addInfoLabel() {

        bottomHBox = new HBox(10);
        bottomHBox.setPrefWidth(width);
        lblStatus = new Label("Your Color:");

        infoLabel = new Label("");
        infoLabel.setStyle("-fx-text-fill: white");
        bottomHBox.getChildren().addAll(lblStatus, infoLabel);
        this.getChildren().add(bottomHBox);
    }

    /**
     * The opponent box contains the score and the names of the opponents.
     *
     * @return VBox
     */
    private VBox createOpponentBox() {
        VBox opponentsBox = new VBox(10);
        opponentsBox.setMinHeight(200);
        opponentsBox.setMinWidth(consoleTile.getSide());
        scoresLabels = new HashMap<>();
        for (Player player : gameModel.getPlayers()) {
            if (player.getPlayerID() != gameModel.getLocalPlayerId()) {
                Label lblOpponentName = new Label(player.getPlayerName());
                lblOpponentName.setStyle("-fx-text-fill: white");
                Label lblOpponentScore = new Label("Score: 0");
                lblOpponentScore.setStyle("-fx-text-fill: white");
                scoresLabels.put(player.getPlayerID(), lblOpponentScore);
                opponentsBox.getChildren().addAll(lblOpponentName, lblOpponentScore);
            }
        }
        return opponentsBox;
    }

    /**
     * Hermann Grieder
     * <br>
     * Adds movement cards to the player during the game.
     *
     * @return HBox filled with the movement cards.
     */
    private HBox placeMovementCards() {
        HBox bottom = new HBox(10);
        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()) {
            styleMovementCard(card);
            bottom.getChildren().add(card);
        }
        return bottom;
    }

    /**
     * Hermann Grieder
     *
     * @param card The movement card to be styled
     */
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
     * @return Hashtable<String, ImageView>
     */
    private Hashtable<String, ImageView> readCardImages() {
        Hashtable<String, ImageView> listCardImages = new Hashtable<>();

        File folder = new File("src/ch/atlantis/res/Spielmaterial/");

        File[] myFiles = folder.listFiles();

        if (myFiles != null) {
            for (File file : myFiles) {
                if (file.exists() && file.isFile()) {
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {

                        //without the substring(4) the path is invalid resp. nullPointerException
                        ImageView imageView = new ImageView(new Image(file.getPath().substring(4)));
                        listCardImages.put(file.getName(), imageView);
                    }
                }
            }
        }
        return listCardImages;
    }

    /**
     * Hermann Grieder
     * <br>
     */
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

    /**
     * Hermann Grieder
     * <br>
     * After every move the infoLabel switches to the right message depending if it is the local players turn or not.
     */
    private void setInfoLblTextOnNewTurn() {
        if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
            //setInfoLabelText("Your turn. Select a game piece and a card and then press move");
            setInfoLabelText(view.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_YourTurn"));
            setLblStatus(view.getSelectedLanguage().getLanguageTable().get("gameBordView_lblStatus") + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getColorName());
        } else {
            //setInfoLabelText(gameModel.getPlayers().get(gameModel.getCurrentTurn()).getPlayerName() + "'s turn. Please wait.");
            setInfoLabelText(gameModel.getPlayers().get(gameModel.getCurrentTurn()).getPlayerName() + view.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_NotYourTurn"));
            setLblStatus(view.getSelectedLanguage().getLanguageTable().get("gameBordView_lblStatus") + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getColorName());
        }
    }


    //*************************** METHODS DURING THE ACTIVE GAME *****************************//

    /**
     * Hermann Grieder
     * <br>
     * Moves the selected game piece to the target path. If the target path is the atlantis land
     * then we move the game piece to the land with an offset. We use the currentPathId of the GamePiece
     * because when the server accepts our move we then set the currentPathId to the value of the targetPathId
     * so they are equivalent.
     *
     * @param selectedGamePiece The gamePiece to move
     */
    public void moveGamePiece(GamePiece selectedGamePiece) {
        int x = 0;
        int y = 0;
        int targetPathId = selectedGamePiece.getCurrentPathId();
        for (Tile targetTile : gameModel.getTiles()) {
            if (targetTile.getPathId() == 400 && targetTile.getPathId() == targetPathId) {
                x = (targetTile.getX() + offsetX + targetTile.getSide() / 3);
                y = (targetTile.getY() + offsetY);
                offsetX = 10;
                offsetY += 15;
            } else if (targetTile.getPathId() == targetPathId) {
                x = targetTile.getX() + (targetTile.getSide() / 2);
                y = targetTile.getY() + (targetTile.getSide() / 2);
            }
        }
        selectedGamePiece.move(x, y);
    }

    /**
     * Hermann Grieder
     * <br>
     * Updates the game Board after every move.
     */
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
                    updatePathCardsStack();
                } else {
                    updateScoreLabel(scoresLabels.get(previousTurn), score);
                }
                setInfoLblTextOnNewTurn();

                //Update the movementCards
                if (gameModel.getLocalPlayerId() == previousTurn) {
                    for (Card card : cards) {
                        styleMovementCard(card);
                        HBoxMovementCards.getChildren().removeAll(card);
                    }
                }
                HBoxMovementCards.getChildren().setAll(gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards());

                //Remove the pathCards
                if (selectedGamePiece.getCurrentPathId() != 101 && gameModel.getIndexOfPathCardToRemove() != -1) {
                    Card pathCardToRemove = gameModel.getPathCards().get(gameModel.getIndexOfPathCardToRemove());
                    removePathCard(pathCardToRemove);
                }
            }
        });
    }

    /**
     * Hermann Grieder
     */
    private void updatePathCardsStack() { // Load score cards in VBox
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stackCardPane.getChildren().setAll(gameModel.getPlayers().get(gameModel.getPreviousTurn()).getPathCardStack());
            }
        });
    }

    /**
     * Fabian Witschi
     * Updates the movement cards if we buy some cards so that we can see them as soon as we get them
     */
    public void updateMovementCards() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (gameModel.getLocalPlayerId() == gameModel.getCurrentTurn()) {
                    for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()) {
                        styleMovementCard(card);
                        HBoxMovementCards.getChildren().removeAll(card);
                    }
                    HBoxMovementCards.getChildren().setAll(gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards());
                }
            }
        });
    }

    /**
     * Hermann Grieder
     *
     * @param scoreLabel The label to be updated
     * @param score      The score that should be shown
     */
    private void updateScoreLabel(Label scoreLabel, int score) {
        logger.info("Total of " + score + " points.");
        logger.info("CurrentTurn: " + gameModel.getCurrentTurn());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //scoreLabel.setText("Score: " + String.valueOf(score));
                //scoreLabel.setText(view.getSelectedLanguage().getLanguageTable().get("gameBordView_lblScoreText") + String.valueOf(score));
                scoreLabel.setText(String.valueOf(score));
            }
        });
    }

    /**
     * Hermann Grieder
     * <br>
     * Toggles the moveButton
     *
     * @param disableButtonMove True if button should be disabled, false if it should be enabled.
     */
    public void setDisableButtonMove(boolean disableButtonMove) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buttonMove.setDisable(disableButtonMove);
            }
        });
    }

    /**
     * Hermann Grieder
     * <br>
     * Toggles the endTurn button
     *
     * @param disableButtonEndTurn True if button should be disabled, false if it should be enabled.
     */
    public void setDisableButtonEndTurn(boolean disableButtonEndTurn) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buttonEndTurn.setDisable(disableButtonEndTurn);
            }
        });
    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the text of the infoLabel
     *
     * @param s The string the infoLabel should show
     */
    public void setInfoLabelText(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                infoLabel.setText(s);
                logger.info(s);
            }
        });
    }

    /**
     * Hermann Grieder
     */
    public void resetCards() {
        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()) {
            card.setOpacity(1);
            card.setDisable(false);
            resetHighlight(card);
        }
    }

    /**
     * Hermann Grieder
     */
    public void createGameOverView() {
        Stage parentStage = gameStage;
        if (gameOverView == null) {
            gameOverView = new GameOverView(gameStage.getHeight(), gameStage.getWidth(), gameModel);
            gameOverStage = new Stage();
            gameOverStage.setScene(new Scene(gameOverView));
            view.setupOverlay(gameOverStage, parentStage, "css_GameOverView");
            view.setXYLocation(gameOverStage, parentStage);
            view.setDimensions(gameOverStage, parentStage);
            gameOverStage.show();
        }

    }

    public void hideGameOver() {
        gameOverStage.hide();
    }

    public void removePathCard(Card pathCard) {
        this.getChildren().remove(pathCard);
    }

    public void showOptions() {
        view.showOptions();
    }


    // ************************************* Styling Methods & CSS ***************************************** //
    // All methods by Hermann Grieder

    void resetHighlight(Rectangle item) {
        item.setStroke(Color.TRANSPARENT);
        item.setStrokeWidth(4);
    }

    void highlightItem(Rectangle item) {
        item.setStroke(Color.LIGHTGREEN);
        item.setStrokeWidth(4);
    }

    void resetHighlight(Circle item) {
        item.setStroke(Color.TRANSPARENT);
        item.setStrokeWidth(4);
    }

    void highlightItem(Circle item) {
        item.setStroke(Color.LIGHTGREEN);
        item.setStrokeWidth(4);
    }

    private void setCSSIds() {
        bottomHBox.setId("bottomHBox");
        lblStatus.setId("gameBordView_lblStatus");

        buttonGameRules.setId("gameBordView_btnGameRules");
        buttonBuyCards.setId("gameBordView_btnBuyCards");
        buttonCantMove.setId("gameBordView_btnCantMove");
        buttonEndTurn.setId("gameBordView_btnEndTurn");
        buttonMove.setId("gameBordView_btnMove");
        buttonPay.setId("gameBordView_btnPay");
        buttonReset.setId("gameBordView_btnReset");

        infoLabel.setId("lblInfo");
        lblScoreLocalPlayer.setId("gameBordView_lblScoreLocalPlayer");
        lblScoreText.setId("gameBordView_lblScoreText");


        //view.getLoginView().getLblError().setText(view.getSelectedLanguage().getLanguageTable().get("login_lblError1"));
    }

    // ************************************* GETTERS / SETTERS ********************************************* //

    public Stage getGameStage() {
        return gameStage;
    }

    public void setLblStatus(String s){
        this.lblStatus.setText(s);
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

    public Button getButtonGameRules() {
        return buttonGameRules;
    }

    public Button getButtonEndTurn() {
        return buttonEndTurn;
    }

    public Button getButtonPay() {
        return buttonPay;
    }

    public Button getButtonCantMove() {
        return buttonCantMove;
    }

    public GameOverView getGameOverView() {
        return gameOverView;
    }

}