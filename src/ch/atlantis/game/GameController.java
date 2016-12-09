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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hermann Grieder on 31.08.2016.
 * The GameController coordinates between the gameModel and the gameBoardView. It handles userInputs and listens
 * to incoming messages in the atlantisModel and to changes in the gameModel.
 */
public class GameController {

    private GameModel gameModel;
    private GameBoardView gameBoardView;
    private AtlantisModel atlantisModel;
    private AtlantisView atlantisView;

    int clickCount;

    public GameController(AtlantisView atlantisView, AtlantisModel atlantisModel, GameModel gameModel, GameBoardView gameBoardView) {
        this.atlantisView = atlantisView;
        this.atlantisModel = atlantisModel;
        this.gameModel = gameModel;
        this.gameBoardView = gameBoardView;
    }

    public void startGame() {
        gameBoardView.show();
        addListeners();
        handleEscapeKey();
        handleMouseEventsMovementCards();
        handleMouseEventsGamePieces();
        handleMouseEventsGameControlButtons();
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
                        gameModel.updateValues();
                        handleMouseEventsMovementCards();
                        gameBoardView.updateBoard();
                        if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                            gameBoardView.getButtonMove().setDisable(false);
                            gameBoardView.getButtonBuyCards().setDisable(false);
                            gameModel.setSelectedCard(null);
                            gameModel.setSelectedGamePiece(null);
                        }
                    }
                }
            }
        });

        gameModel.targetNotOccupiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    gameBoardView.setInfoLabelText("Target is occupied. \nPlay another card to jump over");
                    gameBoardView.setDisableButtonMove(false);
                    gameBoardView.setDisableButtonEndTurn(true);
                }
                gameModel.targetNotOccupiedProperty().set(true);
            }
        });

        gameModel.waterOnTheWayPathIdProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0) {
                    gameBoardView.setInfoLabelText("You need to cross water\nPay with a bridge or with collected cards");
                }
                gameModel.waterOnTheWayPathIdProperty().set(0);
            }
        });
    }

    private void handleEscapeKey() {
        // ********************************* OPTIONS OVERLAY ********************************* //
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

    }

    private void handleMouseEventsGameControlButtons() {
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

                if (clickCount == 0) {
                    gameModel.saveCurrentGameState();
                    System.out.println("GameModel -> Current Game State Saved");
                }
                if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                    clickCount++;
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    gameBoardView.setInfoLabelText("");
                    if (gameModel.canMoveDirectly()) {
                            System.out.println("GameModel -> Move can be done directly");
                            System.out.println("Press \"End Turn\" to confirm your move");
                            gameModel.getSelectedGamePiece().setCurrentPathId(gameModel.getTargetPathId());
                            gameModel.getSelectedCard().setOpacity(0);
                            gameModel.getSelectedCard().setDisable(true);
                            gameBoardView.moveGamePiece();
                            gameBoardView.getButtonMove().setDisable(true);
                            gameBoardView.getButtonEndTurn().setDisable(false);
                            gameBoardView.setInfoLabelText("Press \"End Turn\" to confirm your move");
                    }else{
                        System.out.println("GameModel -> Move can not be done directly");
                    }
                }
            }
        });

        gameBoardView.getButtonReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameBoardView.getButtonMove().isDisabled()) {
                    gameBoardView.getButtonMove().setDisable(false);
                }
                if (gameBoardView.getButtonBuyCards().isDisabled()) {
                    gameBoardView.getButtonBuyCards().setDisable(false);
                }
                if(gameBoardView.getButtonEndTurn().isDisabled()){
                    gameBoardView.getButtonEndTurn().setDisable(false);
                }
                gameBoardView.getButtonReset().setDisable(true);
                gameBoardView.resetHighlight(gameModel.getSelectedCard());
                gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                gameModel.getSelectedGamePiece().resetPathId();
                gameBoardView.moveGamePiece();
                gameBoardView.setInfoLabelText("Your turn\nSelect a game piece and a card");
                gameModel.getSelectedCard().setOpacity(1);
                gameModel.getSelectedCard().setDisable(false);
                gameModel.setSelectedCard(null);
                gameModel.setSelectedGamePiece(null);
                clickCount = 0;
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
                    if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
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
    }

    private void handleMouseEventsGamePieces() {
        ArrayList<GamePiece> gamePieces = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGamePieces();
        for (GamePiece gamePiece : gamePieces ) {
            /*
             * Selects the gamePiece the player clicked on
             */
            gamePiece.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameModel.getSelectedGamePiece() != null) {
                        gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    }
                    if (clickCount == 0 /*&& gameModel.getSelectedGamePiece().getCurrentPathId() != 400 */ ) {
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
    }

    private void handleMouseEventsMovementCards() {
        /*
         * Selects the movement card the player clicked on
         */
        ArrayList<Card> movementCards = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards();

        for (Card card : movementCards) {
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameModel.getSelectedCard() != null) {
                        gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    }
                    gameModel.setSelectedCard(card);
                    System.out.println("GameController -> ColorSet of selected Card: " + gameModel.getSelectedCard().getColorSet());
                    gameBoardView.highlightItem(card);
                }
            });

            /*
             * On mouse enter the movement card will be highlighted.
             */
            card.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for (Card movementCardToReset : movementCards) {
                        if (movementCardToReset != gameModel.getSelectedCard()) {
                            gameBoardView.resetHighlight(movementCardToReset);
                        }
                    }
                    gameBoardView.highlightItem(card);
                }
            });

            /*
             * On mouse exited the movement card will be reset from being highlighted
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
}
