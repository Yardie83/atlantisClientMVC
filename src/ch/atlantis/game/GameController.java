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

    private void sendMoveMessage(HashMap<String, Object> moveMap) {
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
                        gameBoardView.updateBoard();
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
                    new OptionsController(atlantisModel, atlantisView);
                    //gameBoardView.showOptions(atlantisModel.getLanguageList(), atlantisModel.getCurrentLanguage(), gameBoardView.getGameStage());
                }
            }
        });


        // ************************** MOVEMENT CARDS **************************** //

        for (Card movementCard : gameModel.getLocalPlayer().getMovementCards()) {
            /*
             * Selects the movement card the player clicked on
             */
            movementCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameModel.getSelectedCard() != null) {
                        gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    }
                    gameModel.setSelectedCard(movementCard);
                    System.out.println(gameModel.getSelectedCard().getColorSet());
                    gameBoardView.highlightItem(movementCard);
                }
            });

            /*
             * On mouse enter the movement card will be highlighted.
             */
            movementCard.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for (Card movementCardToReset : gameModel.getLocalPlayer().getMovementCards()) {
                        if (movementCardToReset != gameModel.getSelectedCard()) {
                            gameBoardView.resetHighlight(movementCardToReset);
                        }
                    }
                    gameBoardView.highlightItem(movementCard);
                }
            });

            /*
             * On mouse exited the movement card will be reset from being highlighted if it is not
             * the selected movement card
             */
            movementCard.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (movementCard != gameModel.getSelectedCard()) {
                        gameBoardView.resetHighlight(movementCard);
                    }
                }
            });
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
                    gameModel.setSelectedGamePiece(gamePiece);
                    System.out.println(gameModel.getSelectedGamePiece().getCurrentPathId());
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

                clickCount++;
                if (clickCount == 1) {
                    gameModel.savePreviousGameStateMap();
                    System.out.println("GameModel -> Previous Game State Saved");
                }
                if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    gameBoardView.moveGamePiece(gameModel.findTargetPathId(), gameModel.getSelectedGamePiece());
                }
                gameModel.setSelectedCard(null);
                gameModel.setSelectedGamePiece(null);
            }

        });

        gameBoardView.getButtonReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameModel.getPreviousGameStateMap() != null) {
                    gameModel.reloadGameStateMap(gameModel.getPreviousGameStateMap());
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    gameBoardView.moveGamePiece(gameModel.getSelectedGamePiece().getCurrentPathId(), gameModel.getSelectedGamePiece());
                    clickCount = 0;
                }
            }
        });

        gameBoardView.getButtonEndTurn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gameModel.getCurrentTurn() == gameModel.getLocalPlayer().getPlayerID()) {
                    gameBoardView.resetHighlight(gameModel.getSelectedCard());
                    gameBoardView.resetHighlight(gameModel.getSelectedGamePiece());
                    if (gameModel.getSelectedCard() != null && gameModel.getSelectedGamePiece() != null) {
                        gameModel.findTargetPathId();
                        HashMap<String, Object> moveMap = gameModel.writeGameStateMap();
                        sendMoveMessage(moveMap);
                    }
                    gameModel.setSelectedCard(null);
                    gameModel.setSelectedGamePiece(null);
                }
            }
        });
    }
}
