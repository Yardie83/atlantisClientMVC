package ch.atlantis.game;

import ch.atlantis.controller.OptionsController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import ch.atlantis.util.Message;

import java.util.HashMap;

/**
 * Created by Hermann Grieder on 31.08.2016.
 * The GameController coordinates between the gameModel and the gameBoardView. It handles userInputs and listens
 * to incoming messages in the atlantisModel
 */
public class GameController {

    private GameBoardView gameBoardView;
    private AtlantisView atlantisView;
    private GameModel gameModel;
    private AtlantisModel atlantisModel;

    private int clickCount;


    public GameController(AtlantisView atlantisView, AtlantisModel atlantisModel, GameModel gameModel, GameBoardView gameBoardView) {
        this.atlantisView = atlantisView;
        this.atlantisModel = atlantisModel;
        this.gameModel = gameModel;
        this.gameBoardView = gameBoardView;
    }

    // ********************************** METHODS ************************************* //

    public void startGame() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameBoardView.show();
                addListeners();
                handleUserInput();
            }
        });
    }

    private void sendMoveMessage() {
        HashMap<String, Object> moveMap = gameModel.writeGameStateMap();
        atlantisModel.sendMessage(new Message(MessageType.MOVE, moveMap));
    }


    private void addListeners() {

        /*
         * Listens if a move message was received in the atlantisModel
         * then calls the gameModel readInitialGameStateMap method and the gameBoardView update method
         */
        atlantisModel.moveValidProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            @SuppressWarnings("unchecked")
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    if (atlantisModel.getMessage().getMessageObject() instanceof HashMap) {
                        HashMap<String, Object> gameStateMap = (HashMap<String, Object>) atlantisModel.getMessage().getMessageObject();
                        gameModel.readGameStateMap(gameStateMap);
                        handleMouseEvents(gameModel.getNewCardFromDeck());
                        gameModel.updateValues();
                        gameBoardView.updateBoard();
                        gameBoardView.getButtonMove().setDisable(false);
                    }
                }
            }
        });
    }

    private void handleUserInput() {

        /*
         * On KeyPressed Esc the options menu is shown
         */
        //TODO: Make this work
        gameBoardView.getGameStage().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    gameBoardView.showOptions();
                    new OptionsController(atlantisModel, atlantisView);
                }
            }
        });


        // ************************** MOVEMENT CARDS **************************** //

        for (Card movementCard : gameModel.getLocalPlayer().getMovementCards()) {
            handleMouseEvents(movementCard);
        }

        // *********************** GAME PIECES ********************************** //

        for (GamePiece gamePiece : gameModel.getLocalPlayer().getGamePieces()) {
            /*
             * Selects the gamePiece the player clicked on
             */
            gamePiece.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameModel.getSelectedGamePiece() != null) {
                        gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    }
                    if (clickCount == 0) {
                        gameModel.setSelectedGamePiece(gamePiece);
                    }
                    System.out.println("GameController -> GamePiece Current Path Id: " + gameModel.getSelectedGamePiece().getCurrentPathId());
                    gameBoardView.highlightItem(gamePiece);
                }
            });

            /*
             * On mouse enter the gamePiece will be highlighted.
             */
            gamePiece.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gamePiece != gameModel.getSelectedGamePiece()) {
                        gameBoardView.highlightItem(gamePiece);
                    }
                }
            });

            /*
             * On mouse exited the gamePiece will be reset from being highlighted if it is not the
             * selected gamePiece
             */
            gamePiece.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gamePiece != gameModel.getSelectedGamePiece()) {
                        gameBoardView.resetHighlight(gamePiece);
                    }
                }
            });
        }

        //********************************** GAME CONTROL BUTTONS ************************************ //

        gameBoardView.getButtonBuyCards().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        gameBoardView.getButtonMove().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameBoardView.getButtonBuyCards().setDisable(true);
                gameBoardView.getButtonReset().setDisable(false);

                if (clickCount == 1) {
                    gameModel.saveCurrentGameState();
                    System.out.println("GameModel -> Current Game State Saved");
                }
                if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                    clickCount++;
                    gameBoardView.getInfoLabel().setText("");
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    if (gameModel.canMoveDirectly()){
                        gameModel.getSelectedGamePiece().setCurrentPathId(gameModel.getTargetPathId());
                        gameModel.getSelectedCard().setOpacity(0);
                        gameModel.getSelectedCard().setDisable(true);
                        gameBoardView.moveGamePiece();
                        gameBoardView.getButtonMove().setDisable(true);
                        gameBoardView.getButtonEndTurn().setDisable(false);
                        gameBoardView.getInfoLabel().setText("Press End Turn To Confirm Your Move");
                    }
                }
            }
        });

        gameBoardView.getButtonReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameBoardView.getButtonMove().isDisabled()){
                    gameBoardView.getButtonMove().setDisable(false);
                }
                if (gameBoardView.getButtonBuyCards().isDisabled()){
                    gameBoardView.getButtonBuyCards().setDisable(false);
                }
                if (gameModel.getCurrentGameStateMap() != null) {
                    //gameModel.reloadGameStateMap(gameModel.getCurrentGameStateMap());
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    gameModel.getSelectedGamePiece().resetPathId();
                    gameBoardView.moveGamePiece();
                    gameModel.getSelectedCard().setOpacity(1);
                    gameModel.getSelectedCard().setDisable(false);
                    gameModel.setSelectedCard(null);
                    gameModel.setSelectedGamePiece(null);
                    clickCount = 0;
                }
            }
        });

        gameBoardView.getButtonEndTurn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameBoardView.getButtonReset().setDisable(true);
                gameBoardView.getButtonBuyCards().setDisable(true);
                gameBoardView.getButtonMove().setDisable(true);
                gameBoardView.getButtonEndTurn().setDisable(true);
                if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                    if (gameModel.getCurrentTurn() == gameModel.getLocalPlayer().getPlayerID()) {
                        gameBoardView.resetHighlight(gameModel.getSelectedCard());
                        gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                        sendMoveMessage();
                        gameModel.setSelectedCard(null);
                        gameModel.setSelectedGamePiece(null);
                        clickCount = 0;
                    }
                }
            }
        });

        gameModel.targetIsOccupiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    gameBoardView.getInfoLabel().setText("Target is occupied\nPlay another card to jump over");
                }
                gameModel.targetIsOccupiedProperty().set(false);
            }
        });

        gameModel.waterOnTheWayPathIdProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0){
                    gameBoardView.getInfoLabel().setText("You need to cross water\nPay with a bridge or with collected cards");
                }
                gameModel.waterOnTheWayPathIdProperty().set(0);
            }
        });
    }

    private void handleMouseEvents(Card card) {
        /*
         * Selects the movement card the player clicked on
         */
        card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (gameModel.getSelectedCard() != null) {
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                }
                gameModel.setSelectedCard(card);
                System.out.println(gameModel.getSelectedCard().getColorSet());
                gameBoardView.highlightItem(card);
            }
        });

            /*
             * On mouse enter the movement card will be highlighted.
             */
        card.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Card movementCardToReset : gameModel.getLocalPlayer().getMovementCards()) {
                    if (movementCardToReset != gameModel.getSelectedCard()) {
                        gameBoardView.resetHighlight(movementCardToReset);
                    }
                }
                gameBoardView.highlightItem(card);
            }
        });

            /*
             * On mouse exited the movement card will be reset from being highlighted if it is not
             * the selected movement card
             */
        card.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (card != gameModel.getSelectedCard()) {
                    gameBoardView.resetHighlight(card);
                }
            }
        });
    }
}
