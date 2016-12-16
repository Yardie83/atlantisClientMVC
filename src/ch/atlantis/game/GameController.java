package ch.atlantis.game;

import ch.atlantis.AtlantisClient;
import ch.atlantis.controller.OptionsController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.logging.Logger;

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
    private SimpleBooleanProperty gameOver;

    private int clickCount;

    private Logger logger;

    public GameController(AtlantisView atlantisView, AtlantisModel atlantisModel, GameModel gameModel, GameBoardView gameBoardView) {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        this.atlantisView = atlantisView;
        this.atlantisModel = atlantisModel;
        this.gameModel = gameModel;
        this.gameBoardView = gameBoardView;
        gameOver = new SimpleBooleanProperty(false);
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
                        if (gameModel.readGameStateMap(gameStateMap)) {
                            gameModel.updateValues();
                            handleMouseEventsMovementCards();
                            handleMouseEventsStackCards();
                            gameBoardView.updateBoard();
                            if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                                gameBoardView.setDisableButtonMove(false);
                                gameBoardView.getButtonBuyCards().setDisable(true);
                                gameModel.setSelectedCard(null);
                                gameModel.setSelectedGamePiece(null);
                                gameModel.setTargetPathIds(null);
                                gameModel.clearPaidCardsIndices();
                            }
                        }
                    }
                }
            }
        });

        /**
         * Can Heval Cokyasar
         *
         */

        atlantisModel.givePurchasedCards().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    if (atlantisModel.getMessage().getMessageObject() instanceof ArrayList) {
                        ArrayList<Card> arrayListOfPurchasedCards = (ArrayList<Card>) atlantisModel.getMessage().getMessageObject();
                        if (arrayListOfPurchasedCards.size() != 0) {
                            for (Card card : arrayListOfPurchasedCards) {
                                System.out.println("IN LISTENER FOR BUYING CARDS -> " + card);
                                gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().add(card);
                                System.out.println("SIZE OF MOVEMENTCARDS AFTER ADDING CARD TO PLAYER -> " + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().size());
                            }
                            gameBoardView.updateMovementCards();
                            handleMouseEventsMovementCards();
                            atlantisModel.givePurchasedCards().setValue(false);
                            gameBoardView.getButtonBuyCards().setDisable(true);
                            gameBoardView.setInfoLabelText("You got a new Card");
                            arrayListOfPurchasedCards.clear();
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
                }
            }
        });

        /**
         * Fabian Witschi
         *
         */

        gameModel.priceToCrossWaterProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0) {
                    gameBoardView.setInfoLabelText("You have to pay: " + newValue + " to cross");
                    gameBoardView.setDisableButtonEndTurn(true);
                    gameBoardView.setDisableButtonMove(true);
                    gameBoardView.getButtonPay().setDisable(false);
                }
            }
        });
    }

    /**
     * Can Heval Cokyasar
     */

    private void handleMouseEventsStackCards() {

        ArrayList<Card> pathCardStack = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack();

        for (Card card : pathCardStack) {
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameModel.priceToCrossWaterProperty().get() != 0) {
                        int index = pathCardStack.indexOf(card);
                        if (gameModel.getPaidCardIndices() != null && gameModel.getPaidCardIndices().contains(index)) {
                            gameBoardView.resetHighlight(card);
                            System.out.println("Index: " + index + " trying to remove");
                            gameModel.getPaidCardIndices().remove(index);
                            System.out.println("Index: " + index + " removed");
                        } else {
                            System.out.println("Index: " + index + " trying to add");
                            gameModel.getPaidCardIndices().add(index);
                            System.out.println("Index: " + index + " added");
                            gameBoardView.highlightItem(card);
                        }
                    } else if (gameModel.getSelectedStackCard() != null) {
                        gameBoardView.setInfoLabelText("");
                        gameBoardView.resetHighlight(gameModel.getSelectedStackCard());

                        if (clickCount == 0) {
                            if (card.getValue() > 1) {
                                gameBoardView.setInfoLabelText("You selected a card of value: " + card.getValue() + "\n" +
                                        "You get " + (card.getValue() / 2) + " cards, press \"Buy Cards\".");
                                gameBoardView.getButtonBuyCards().setDisable(false);
                            }
                            if (card.getValue() == 1 && (!gameBoardView.getButtonBuyCards().isDisabled())) {
                                gameBoardView.getButtonBuyCards().setDisable(true);
                                gameBoardView.setInfoLabelText("The selected card's value is too low to buy a card");
                            }
                        }
                        gameModel.setSelectedStackCard(card);
                        gameModel.setSelectedStackCardIndex(pathCardStack.indexOf(card));
                        logger.info("GameController -> You selected the card of index of " + gameModel.getSelectedStackCardIndex());
                        gameBoardView.highlightItem(card);
                    }
                }
            });

            // Highlight stack card when entered
            card.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (card != gameModel.getSelectedStackCard()) {
                        gameBoardView.highlightItem(card);
                    }
                }
            });

            // Reset highlight stack card when exited
            card.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (card != gameModel.getSelectedStackCard() && gameModel.getPaidCardIndices() != null && !gameModel.getPaidCardIndices().contains(card)) {
                        gameBoardView.resetHighlight(card);
                    }
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

        /**
         * Fabian Witschi
         *
         */

        gameBoardView.getButtonPay().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameModel.getPaidCardIndices().size() != 0) {
                    if (gameModel.hasPaidCorrectPrice()) {
                        gameBoardView.getButtonPay().setDisable(true);
                        for (int index : gameModel.getPaidCardIndices()) {
                            Card card = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack().get(index);
                            card.setOpacity(0);
                            card.setDisable(true);
                        }
                        tryToMove();
                    } else {
                        gameModel.clearPaidCardsIndices();
                        System.out.println("Paid Cards Index cleared");
                        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack()) {
                            gameBoardView.resetHighlight(card);
                        }
                        gameBoardView.setInfoLabelText("Sorry amount is not sufficient");
                    }
                } else

                {
                    gameBoardView.setInfoLabelText("Select a card to pay with");
                }
                gameModel.setPaidCorrectPrice(false);
            }
        });

        /**
         * Can Heval Cokyasar
         *
         */
        gameBoardView.getButtonBuyCards().

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (gameModel.getSelectedStackCardIndex() != -1) {
                            HashMap<String, Object> hashToBuyCards = new HashMap<>();
                            String gameName = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGameName();
                            Integer indexToSend = gameModel.getSelectedStackCardIndex();
                            hashToBuyCards.put("GameName", gameName);
                            hashToBuyCards.put("Index", indexToSend);
                            atlantisModel.sendMessage(new Message(MessageType.BUYCARD, hashToBuyCards)); // Send message to server
                        }
                    }
                });

        gameBoardView.getButtonMove().

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (clickCount == 0) {
                            for (GamePiece gamePiece : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGamePieces()) {
                                gamePiece.setStartPathId(gamePiece.getCurrentPathId());
                                logger.info("GameModel -> Start path IDs set.");
                                gameModel.getPlayedCardsIndices().clear();
                                logger.info("Played card indices cleared.");
                            }
                        }
                        tryToMove();
                    }
                });

        gameBoardView.getButtonReset().

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (gameBoardView.getButtonMove().isDisabled()) {
                            gameBoardView.setDisableButtonMove(false);
                        }
                        if (!gameBoardView.getButtonEndTurn().isDisabled()) {
                            gameBoardView.setDisableButtonEndTurn(true);
                        }
                        if (!gameBoardView.getButtonPay().isDisabled()) {
                            gameBoardView.getButtonPay().setDisable(true);
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

        gameBoardView.getButtonEndTurn().

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        gameBoardView.getButtonReset().setDisable(true);
                        gameBoardView.getButtonBuyCards().setDisable(true);
                        gameBoardView.setDisableButtonMove(true);
                        gameBoardView.setDisableButtonEndTurn(true);
                        gameBoardView.getButtonPay().setDisable(true);
                        if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                            if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                                gameBoardView.resetHighlight(gameModel.getSelectedCard());
                                gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                                logger.info("GameController -> End of turn of player " + gameModel.getLocalPlayerId());
                                clickCount = 0;
                                gameModel.getSelectedGamePiece().setCurrentPathId(gameModel.getSelectedGamePiece().getStartPathId());
                                sendMoveMessage();
                            }
                        }
                    }
                });

        gameBoardView.getButtonGameRules().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                atlantisView.getGameLobbyView().showGameRules();
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
            gameModel.occupiedProperty().set(false);
            if (gameModel.canMoveDirectly()) {
                logger.info("GameModel -> Mode can be done directly.");
                gameBoardView.setDisableButtonMove(true);
                gameBoardView.setDisableButtonEndTurn(false);
                gameBoardView.setInfoLabelText("Press \"End Turn\" to confirm your move");
                gameModel.getSelectedGamePiece().setCurrentPathId(gameModel.getTargetPathId());
            } else {
                logger.info("GameModel -> Move cannot be done directly.");
            }
            gameModel.getSelectedCard().setOpacity(0);
            gameModel.getSelectedCard().setDisable(true);
            gameModel.addToPlayedCards();
            gameBoardView.moveGamePiece();
        } else if (gameModel.getSelectedCard() == null && gameModel.getSelectedGamePiece() != null) {
            gameBoardView.setInfoLabelText("Please select a card to play");
        } else if (gameModel.getSelectedGamePiece() == null && gameModel.getSelectedCard() != null) {
            gameBoardView.setInfoLabelText("Please select a game piece to play");
        }
    }

    private void handleGameOver() {
        gameBoardView.createGameOverView();
        gameBoardView.showGameOver();
        backToLobbyButtonHandler();
    }

    private void backToLobbyButtonHandler() {
        gameBoardView.getGameOverView().getBtnBackToLobby().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameOver.set(true);
                gameBoardView.hideGameOver();
            }
        });
    }

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
                        logger.info("GameController -> GamePiece selected.");
                        gameModel.setSelectedGamePiece(gamePiece);
                    }
                    logger.info("GameController -> GamePiece current path ID: " + gameModel.getSelectedGamePiece().getCurrentPathId());
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
                    logger.info("Index of selected card: " + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().indexOf(card));
                    logger.info("GameController -> ColorSet of selected card: " + gameModel.getSelectedCard().getColorSet());
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

    public SimpleBooleanProperty gameOverProperty() {
        return gameOver;
    }
}
