package ch.atlantis.game;

import ch.atlantis.controller.OptionsController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
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
    private

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
         * Listens if a move message was received in the atlantisModel. Will be executed for all player moves.
         */
        atlantisModel.moveValidProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            @SuppressWarnings("unchecked")
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    if (atlantisModel.getMessage().getMessageObject() instanceof HashMap) {
                        HashMap<String, Object> gameStateMap = (HashMap<String, Object>) atlantisModel.getMessage().getMessageObject();
                        if(gameModel.readGameStateMap(gameStateMap)) {
                            gameModel.updateValues();
                            handleMouseEventsMovementCards();
                            handleMouseEventsStackCards();
                            gameBoardView.updateBoard();
                            if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                                gameBoardView.setDisableButtonMove(false);
                                gameBoardView.getButtonBuyCards().setDisable(false);
                                gameModel.setSelectedCard(null);
                                gameModel.setSelectedGamePiece(null);
                                gameModel.setTargetPathIds(null);
                                gameModel.clearPaidCardsIndex();
                            }
                        }
                    }
                }
            }
        });

        gameModel.occupiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    gameBoardView.setInfoLabelText("Target is occupied. \nPlay another card to jump over");
                    gameBoardView.setDisableButtonMove(false);
                    gameBoardView.setDisableButtonEndTurn(true);
                    gameModel.occupiedProperty().set(false);
                }
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

        gameModel.priceToCrossWaterProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0){
                    gameBoardView.setInfoLabelText("You have to pay: " + newValue + " to cross");
                    gameBoardView.setDisableButtonMove(true);
                }
                gameModel.priceToCrossWaterProperty().set(0);
            }
        });
    }

    private void handleMouseEventsStackCards() {

        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack()){
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(card != gameModel.getSelectedStackCard()){
                        gameBoardView.resetHighlight(card);
                    }
                    gameModel.setSelectedStackCard(card);
                    gameBoardView.highlightItem(card);
                }
            });
        }
    }

    private void handleEscapeKey() {
        // ********************************* OPTIONS OVERLAY ********************************* //

        /*
         * On KeyPressed Esc the options menu is shown
         */
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

        gameBoardView.getButtonPay().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(gameModel.getSelectedStackCard() != null){
                    gameModel.payForCrossing();
                    tryToMove();
                }
            }
        });

        gameBoardView.getButtonBuyCards().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Code
            }
        });

        gameBoardView.getButtonMove().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (clickCount == 0) {
                    for (GamePiece gamePiece : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGamePieces()) {
                        gamePiece.setStartPathId(gamePiece.getCurrentPathId());
                        gameModel.getPlayedCardsIndices().clear();
                    }
                    System.out.println("GameModel -> Start path Ids set");
                }
                tryToMove();
            }
        });

        gameBoardView.getButtonReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameBoardView.getButtonMove().isDisabled()) {
                    gameBoardView.setDisableButtonMove(false);
                }
                if (gameBoardView.getButtonBuyCards().isDisabled()) {
                    gameBoardView.getButtonBuyCards().setDisable(false);
                }
                if (gameBoardView.getButtonEndTurn().isDisabled()) {
                    gameBoardView.setDisableButtonEndTurn(false);
                }
                gameBoardView.getButtonReset().setDisable(true);
                gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                gameModel.getSelectedGamePiece().resetPathId();
                gameBoardView.moveGamePiece();
                gameBoardView.resetCards();
                gameModel.setSelectedCard(null);
                gameModel.getPlayedCardsIndices().clear();
                gameModel.setTargetPathIds(null);
                gameBoardView.setInfoLabelText("Your turn\nSelect a game piece and a card");
                clickCount = 0;
            }
        });

        gameBoardView.getButtonEndTurn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameBoardView.getButtonReset().setDisable(true);
                gameBoardView.getButtonBuyCards().setDisable(true);
                gameBoardView.setDisableButtonMove(true);
                gameBoardView.setDisableButtonEndTurn(true);
                if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                    if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                        gameBoardView.resetHighlight(gameModel.getSelectedCard());
                        gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                        System.out.println("GameController -> End of Turn of player " + gameModel.getLocalPlayerId());
                        clickCount = 0;
                        sendMoveMessage();
                    }
                }
            }
        });
    }

    private void tryToMove() {
        gameBoardView.getButtonBuyCards().setDisable(true);
        gameBoardView.getButtonReset().setDisable(false);

        if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
            clickCount++;
            gameBoardView.resetHighlight(gameModel.getSelectedCard());
            gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
            gameBoardView.setInfoLabelText("");
            if (gameModel.canMoveDirectly()) {
                System.out.println("GameModel -> Move can be done directly");
                gameBoardView.setDisableButtonMove(true);
                gameBoardView.setDisableButtonEndTurn(false);
                gameBoardView.setInfoLabelText("Press \"End Turn\" to confirm your move");
                gameModel.getSelectedCard().setOpacity(0);
                gameModel.getSelectedCard().setDisable(true);
                gameModel.getSelectedGamePiece().setCurrentPathId(gameModel.getTargetPathId());
                gameBoardView.moveGamePiece();
                gameModel.addToPlayedCards();
            } else {
                System.out.println("GameModel -> Move can not be done directly");
            }
        }
    }

    private void handleGameOver() {
        gameBoardView.createGameOverView();
        gameBoardView.showGameOver();
        //backToLobbyButtonHandler();
    }

//    private void backToLobbyButtonHandler() {
//        gameBoardView.getGameOverView().getButtonBackToLobby().setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//            }
//        });
//    }

    private void handleMouseEventsGamePieces() {
        ArrayList<GamePiece> gamePieces = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGamePieces();
        for (GamePiece gamePiece : gamePieces) {
            /*
             * Selects the gamePiece the player clicked on
             */
            gamePiece.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameModel.getSelectedGamePiece() != null) {
                        gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    }
                    if (clickCount == 0 && gamePiece.getCurrentPathId() != 400) {
                        System.out.println("GameController -> GamePiece selected");
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
                    System.out.println("Index of selected card: " + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().indexOf(card));
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
