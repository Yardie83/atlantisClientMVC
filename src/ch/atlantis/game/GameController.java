package ch.atlantis.game;

import ch.atlantis.AtlantisClient;
import ch.atlantis.controller.OptionsController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
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
import java.util.Map;
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

    /**
     * Hermann Grieder
     * <br>
     * Sends the moveMap from the gameModel at the end of a turn to the server
     */
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
                if (newValue && atlantisModel.getMessage().getMessageObject() instanceof HashMap) {
                    HashMap<String, Object> gameStateMap = (HashMap<String, Object>) atlantisModel.getMessage().getMessageObject();
                    if (gameModel.readGameStateMap(gameStateMap)) {
                        System.out.println("The size of played cards when arrives " + gameModel.getPlayedCardsIndices().size());
                        gameModel.updateValues();
                        handleMouseEventsMovementCards();
                        handleMouseEventsStackCards();
                        gameBoardView.updateBoard();
                        if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                            updateLocalValues();
                        }
                    }
                }
            }
        });

        /**
         * Fabian Witschi
         * If we can't move any of our gamepiece there is a button that sends us two cards and this listener
         * changes to true if we receive such a "cant move" message and gets the two cards from the arraylist
         * we send from the server to the client.
         *
         */

        atlantisModel.cardsForNotMoving().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue && atlantisModel.getMessage().getMessageObject() instanceof ArrayList) {
                    ArrayList<Card> listOfCantMove = (ArrayList<Card>) atlantisModel.getMessage().getMessageObject();
                    if (listOfCantMove.size() != 0) {
                        for (Card card : listOfCantMove) {
                            logger.info("Card received from the server - > " + card);
                            gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().add(card);
                        }
                        logger.info("Size of cards after receiving all of the cards - > " + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().size());
                        gameBoardView.updateMovementCards();
                        handleMouseEventsMovementCards();
                        //gameBoardView.setInfoLabelText("You got two new cards");
                        gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_TwoNewCards"));
                    }
                }
            }
        });

        /**
         * Fabian Witschi
         * Just right after receiving two new cards for not moving we change the new turn to the next one
         * since the rules say that after you received two cards your turn is over. It gets the new incremented
         * turn on the client and my turn is over.
         *
         */

        atlantisModel.newTurn().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    int newTurn = (Integer) atlantisModel.getMessage().getMessageObject();
                    gameModel.setPreviousTurn(gameModel.getCurrentTurn());
                    logger.info("Previous turn was - > " + gameModel.getPreviousTurn());
                    gameModel.setCurrentTurn(newTurn);
                    logger.info("Current turn is - > " + gameModel.getCurrentTurn());
                    gameBoardView.updateBoard();
                    if (gameModel.getCurrentTurn() == gameModel.getLocalPlayerId()) {
                        updateLocalValues();
                    }
                }
            }
        });

        /**
         * Can Heval Cokyasar
         *
         * Add the purchased cards of a user to it's hand cards.
         */

        atlantisModel.givePurchasedCards().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue && atlantisModel.getMessage().getMessageObject() instanceof ArrayList) {
                    ArrayList<Card> arrayListOfPurchasedCards = (ArrayList<Card>) atlantisModel.getMessage().getMessageObject();
                    if (arrayListOfPurchasedCards.size() != 0) {
                        for (Card card : arrayListOfPurchasedCards) {
                            gameModel.getPlayers().get(gameModel.getCurrentTurn()).getMovementCards().add(card);
                        }
                        System.out.println("SIZE OF CARDS -- > " + gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards().size());
                        gameBoardView.updateMovementCards();
                        handleMouseEventsMovementCards();
                        gameBoardView.getButtonBuyCards().setDisable(true);
                        gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack().remove(gameModel.getSelectedStackCardIndex());
                        //gameBoardView.setInfoLabelText("You got (a) new Card(s)");
                        gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_NewCards"));
                        gameBoardView.setInfoLabelText("You got " + arrayListOfPurchasedCards.size() + " new Card(s)");
                    }
                }
            }
        });

        /**
         * Hermann Grieder
         *
         */

        atlantisModel.gameOverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    handleGameOver();
                }
            }
        });

        /**
         * Fabian Witschi
         * Before the game is over we send the final score of the clients to each client and iterate through the scores
         * and so we can update the score of each player and when the gameover message (true) comes in we have the right
         * scores
         *
         */

        atlantisModel.gameOverScores().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    HashMap<Integer, Integer> mapOfScores = (HashMap<Integer, Integer>) atlantisModel.getMessage().getMessageObject();
                    for (Map.Entry<Integer, Integer> entry : mapOfScores.entrySet()) {
                        int playerId = entry.getKey();
                        gameModel.getPlayers().get(playerId).setScore(entry.getValue());
                    }
                }
            }
        });

        /**
         * Hermann Grieder
         *
         */

        gameModel.occupiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    //gameBoardView.setInfoLabelText("Target is occupied. Play another card to jump over");
                    gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_OccupiedTarget"));
                    gameBoardView.setDisableButtonMove(false);
                    gameBoardView.setDisableButtonEndTurn(true);
                }
            }
        });

        /**
         * Fabian Witschi
         * If the price has changed to a certain number this listener is giving the user the information about the price
         * and disables/enables some buttons which then give the right environment for paying with cards
         */

        gameModel.priceToCrossWaterProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0) {
                    //gameBoardView.setInfoLabelText("You have to pay: " + newValue + " to cross");
                    gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_PayToCross1") + newValue +
                            atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_PayToCross2"));
                    gameBoardView.setDisableButtonEndTurn(true);
                    gameBoardView.setDisableButtonMove(true);
                    gameBoardView.getButtonPay().setDisable(false);
                }
            }
        });

        /**
         * Fabian Witschi
         * If the button "cant move" is pressed a boolean will be set to true and thus a different listener is activated since
         * we want to have an independent environment for the "automatical" move. The listener goes automatically through
         * all the path cards that we gathered during the game and stops if we get the right amount to cross -> it tells then
         * the user that he/she has enough money and obviously enough cards to move and is not allowed to get two cards from
         * the deck
         */

        gameModel.priceToCrossWaterAutomatically().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0) {
                    int sumValue = 0;
                    for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack()) {
                        sumValue += card.getValue();
                        if (sumValue >= newValue.intValue()) {
                            gameModel.setPaidCorrectPrice(true);
                        }
                    }
                }
            }
        });
    }

    /**
     * Can Heval Cokyasar & Hermann Grieder
     *
     * Being able for a user to select it's picked up stack cards in order to buy additional hand cards.
     */

    private void handleMouseEventsStackCards() {

        ArrayList<Card> pathCardStack = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack();

        for (Card card : pathCardStack) {
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    gameModel.setSelectedStackCard(card);
                    if (gameModel.priceToCrossWaterProperty().get() != 0) {
                        int index = pathCardStack.indexOf(card);
                        if (gameModel.getPaidCardIndices() != null && gameModel.getPaidCardIndices().contains(index)) {
                            gameBoardView.resetHighlight(card);
                            logger.info("Index: " + index + " trying to remove");
                            gameModel.getPaidCardIndices().remove(index);
                            logger.info("Index: " + index + " removed");
                        } else {
                            logger.info("Index: " + index + " trying to add");
                            gameModel.getPaidCardIndices().add(index);
                            logger.info("Index: " + index + " added");
                            gameBoardView.highlightItem(card);
                        }
                    } else if (gameModel.getSelectedStackCard() != null) {
                        gameBoardView.setInfoLabelText("");
                        gameBoardView.resetHighlight(gameModel.getSelectedStackCard());

                        if (clickCount == 0 && gameModel.getLocalPlayerId() == gameModel.getCurrentTurn()) {
                            if (card.getValue() > 1) {
                                //gameBoardView.setInfoLabelText("You selected a card of value: " + card.getValue() + ". " +
                                        //"You get " + (card.getValue() / 2) + " cards, press \"Buy Cards\".");

                                gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_BuyCards1")
                                        + card.getValue() + ". " +
                                        atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_BuyCards2")
                                        + (card.getValue() / 2) + atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_BuyCards3"));

                                gameBoardView.getButtonBuyCards().setDisable(false);
                            }
                            if (card.getValue() == 1 && (!gameBoardView.getButtonBuyCards().isDisabled())) {
                                gameBoardView.getButtonBuyCards().setDisable(true);
                                //gameBoardView.setInfoLabelText("The selected card's value is too low to buy a card");
                                gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_CardValueTooLow"));
                            }
                        }
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
         * If we can't move we have due the rules the option to get two new movement cards. But it is possible to cheat
         * and getting two new cards even though you are able to move is not fair to others the button calls the
         * "canmoveautomatically" method and it checks if the player is not able to move at all and if so the action method
         * will send a message which then gives two cards back from the server. Otherswise we will be notified by a label that
         * we are able to move and we wont get any card.
         *
         * NOTE : Since i could not check the method "tryMoveAutomatically" it might be that some problems occur so if so
         * just change the condition in the if statement from tryToMoveAutomatically to true and so we always get two cards
         * (this works since i have tried it several times)
         *
         */

        gameBoardView.getButtonCantMove().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameModel.setCantMoveButtonHasBeenPressed(true);
                if (!(tryMoveAutomatically())) {
                    //gameBoardView.setInfoLabelText("You will get two cards");
                    gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_WillGet2Cards"));
                    gameBoardView.getButtonMove().setDisable(true);
                    atlantisModel.sendMessage(new Message(MessageType.CANTMOVE, gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGameName()));
                } else {
                    //gameBoardView.setInfoLabelText("You CAN move with your cards");
                    gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_YouCanMove"));
                }
                gameBoardView.getButtonCantMove().setDisable(true);
                gameModel.setPaidCorrectPrice(false);
                gameModel.getTargetPathIds().clear();
                gameModel.getPlayedCardsIndices().clear();
                gameModel.getPaidCardIndices().clear();
                gameModel.setSelectedCard(null);
                gameModel.setSelectedGamePiece(null);
                gameModel.occupiedProperty().setValue(false);
                gameModel.setCantMoveButtonHasBeenPressed(false);
            }
        });

        /**
         * Fabian Witschi
         * If we have to pay to cross water we are requested to select pathcards in order to pay the right amount. The
         * button pay calls a method if the paid amount is enough to cross and gives either the message the price is not enough
         * or it makes the cards invisible until we end the move. Then if we paid the right amount a boolean will be set true
         * and the tryToMove will be called again but if we paid the right amount it wont check the water again and we can move.
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
                        logger.info("Paid Cards Index cleared");
                        for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getPathCardStack()) {
                            gameBoardView.resetHighlight(card);
                        }
                        //gameBoardView.setInfoLabelText("Sorry amount is not sufficient. Price to cross " + gameModel.priceToCrossWaterProperty());
                        gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_AmountNotSufficient")
                                + gameModel.priceToCrossWaterProperty());
                    }
                } else {
                    //gameBoardView.setInfoLabelText("Select a card to pay with");
                    gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_SelectCardToPay"));
                }
                gameModel.setPaidCorrectPrice(false);
            }
        });

        /**
         * Can Heval Cokyasar
         *
         * Configure the "Buy Cards" button.
         * Sending the message object to the AtlantisServer.
         */
        gameBoardView.getButtonBuyCards().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameModel.getSelectedStackCardIndex() != -1) {
                    HashMap<String, Object> hashToBuyCards = new HashMap<>();
                    String gameName = gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGameName();
                    Integer indexToSend = gameModel.getSelectedStackCardIndex();
                    hashToBuyCards.put("GameName", gameName);
                    hashToBuyCards.put("Index", indexToSend);
                    Card card = gameModel.getSelectedStackCard();
                    card.setOpacity(0);
                    card.setDisable(true);
                    atlantisModel.sendMessage(new Message(MessageType.BUYCARD, hashToBuyCards)); // Send message to server
                }
            }
        });

        /**
         * Hermann Grieder
         *
         */

        gameBoardView.getButtonMove().setOnAction(new EventHandler<ActionEvent>() {
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

        /**
         * Hermann Grieder
         *
         */

        gameBoardView.getButtonReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameBoardView.getButtonMove().isDisabled()) {
                    gameBoardView.setDisableButtonMove(false);
                }
                if (gameBoardView.getButtonCantMove().isDisabled()) {
                    gameBoardView.getButtonCantMove().setDisable(false);
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
                gameBoardView.moveGamePiece(gameModel.getSelectedGamePiece());
                gameBoardView.resetCards();
                gameModel.setSelectedCard(null);
                gameModel.setSelectedGamePiece(null);
                gameModel.getPaidCardIndices().clear();
                gameModel.setTargetPathIds(null);
                //gameBoardView.setInfoLabelText("Your turn. Select a game piece and a card");
                gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_YourTurn"));
                clickCount = 0;
            }
        });

        /**
         * Hermann Grieder
         *
         */

        gameBoardView.getButtonEndTurn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameBoardView.getButtonReset().setDisable(true);
                gameBoardView.getButtonBuyCards().setDisable(true);
                gameBoardView.setDisableButtonMove(true);
                gameBoardView.setDisableButtonEndTurn(true);
                gameBoardView.getButtonPay().setDisable(true);
                gameBoardView.getButtonCantMove().setDisable(true);
                gameBoardView.resetHighlight(gameModel.getSelectedCard());
                gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
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
                atlantisModel.showGameRules();
            }
        });
    }

    /**
     * Fabian Witschi
     * It does nothing else than the "tryToMove" but this one is done automatically. If we cant move this method is called and
     * selects for each gamepiece all the cards as long as needed in order to check if we can move or not. If we can then we
     * will be notified that we are able to move with our cards and otherwise we will get the two cards which handles the button
     * above. If we have enough movement cards but have to cross water/or it is occupied the method should pay or move foreward
     * automatically.
     *
     * NOTE : Unfortunately i could never produce such a case and therefore not test the method if it works fine.
     *
     * @return
     */

    private boolean tryMoveAutomatically() {

        for (GamePiece gamePiece : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getGamePieces()) {
            gameModel.setSelectedGamePiece(gamePiece);
            GamePiece tempSavingGamePiece = gamePiece;
            for (Card card : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()) {
                gameModel.setSelectedCard(card);
                if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                    gameModel.occupiedProperty().set(false);
                    if (gameModel.canMoveDirectly()) {
                        logger.info("Boolean value is -> " + true);
                        return true;
                    }
                    gameModel.getSelectedCard().setDisable(true);
                    gameModel.addToPlayedCards();
                    if (gameModel.occupiedProperty().get() == true) {
                        for (Card anotherCard : gameModel.getPlayers().get(gameModel.getLocalPlayerId()).getMovementCards()) {
                            if (anotherCard != gameModel.getSelectedCard()) {
                                gameModel.setSelectedCard(anotherCard);
                                gameBoardView.moveGamePiece(gameModel.getSelectedGamePiece());
                                if (gameModel.canMoveDirectly()) {
                                    gameModel.occupiedProperty().setValue(false);
                                    gameModel.getSelectedGamePiece().resetPathId();
                                    gameBoardView.moveGamePiece(gameModel.getSelectedGamePiece());
                                    return true;
                                }
                            }
                        }
                    }
                    if (gameModel.priceToCrossWaterAutomatically().get() != 0) {
                        if (gameModel.canMoveDirectly()) {
                            gameModel.priceToCrossWaterAutomatically().setValue(0);
                            return true;
                        }
                    }
                }
            }
        }
        logger.info("Boolean value is -> " + false);
        return false;
    }

    /**
     * Hermann Grieder
     *
     */

    private void tryToMove() {
        gameBoardView.getButtonBuyCards().setDisable(true);
        gameBoardView.getButtonReset().setDisable(false);

        if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
            clickCount++;
            gameBoardView.setInfoLabelText("");
            gameModel.occupiedProperty().set(false);
            if (gameModel.canMoveDirectly()) {
                logger.info("GameModel -> Move can be done directly.");
                gameModel.addToPlayedCards();
                gameBoardView.setDisableButtonMove(true);
                gameBoardView.setDisableButtonEndTurn(false);
                //gameBoardView.setInfoLabelText("Press \"End Turn\" to confirm your move");
                gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_ConfirmMove"));
                gameModel.getSelectedGamePiece().setCurrentPathId(gameModel.getTargetPathId());
            } else {
                logger.info("GameModel -> Move cannot be done directly.");
            }
            gameModel.getSelectedCard().setOpacity(0);
            gameModel.getSelectedCard().setDisable(true);
            gameBoardView.moveGamePiece(gameModel.getSelectedGamePiece());
        } else if (gameModel.getSelectedGamePiece() == null && gameModel.getSelectedCard() == null) {
            //gameBoardView.setInfoLabelText("Please select a card and a game piece to play and then press move");
            gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_YourTurn"));
        } else if (gameModel.getSelectedCard() == null) {
            //gameBoardView.setInfoLabelText("Please select a card to play and then press move");
            gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_SelectCardPressMove"));
        } else if (gameModel.getSelectedGamePiece() == null) {
            //gameBoardView.setInfoLabelText("Please select a game piece to play and then press move");
            gameBoardView.setInfoLabelText(atlantisView.getSelectedLanguage().getLanguageTable().get("gameBordView_InfoLabel_SelectGamePiecePressMove"));
        }
    }

    /**
     * Hermann Grieder
     *
     */

    private void handleGameOver() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameBoardView.createGameOverView();
                backToLobbyButtonHandler();
            }
        });

    }

    /**
     * Hermann Grieder
     *
     */

    private void backToLobbyButtonHandler() {
        gameBoardView.getGameOverView().getBtnBackToLobby().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameOver.set(true);
                gameBoardView.hideGameOver();
            }
        });
    }

    /**
     * Hermann Grieder
     *
     */

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

    /**
     * Hermann Grieder
     *
     */

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

    /**
     * Fabian Witschi
     * Updates the local values
     *
     */

    private void updateLocalValues() {
        gameBoardView.setDisableButtonMove(false);
        gameBoardView.getButtonCantMove().setDisable(false);
        gameBoardView.getButtonBuyCards().setDisable(true);
        gameModel.setSelectedCard(null);
        gameModel.setSelectedGamePiece(null);
        gameModel.setTargetPathIds(null);
        gameModel.clearPaidCardsIndices();
    }

    public SimpleBooleanProperty gameOverProperty() {
        return gameOver;
    }
}
