package ch.atlantis.game;

import ch.atlantis.controller.OptionsController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.util.MessageType;
import ch.atlantis.view.AtlantisView;
import javafx.application.Platform;
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
 */
public class GameController {

    private GameBoardView gameBoardView;
    private AtlantisView atlantisView;
    private GameModel gameModel;
    private AtlantisModel atlantisModel;
    private Card selectedCard;
    private GamePiece selectedGamePiece;
    private Card cardToMove;
    private int cardBehindPathId;
    private int cardToMoveId;
    private int tempColorSet;
    private ArrayList<Card> pathCards;
    private ArrayList<Card> movementCards;
    private int playerId;
    private Message message;
    private int playerTurn = 0;

    public GameController(AtlantisView atlantisView, AtlantisModel atlantisModel, GameModel gameModel, GameBoardView gameBoardView) {
        this.atlantisView = atlantisView;
        this.atlantisModel = atlantisModel;
        this.gameModel = gameModel;
        this.gameBoardView = gameBoardView;
    }


    public void startListeners() {

        gameBoardView.getGameStage().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    new OptionsController(atlantisModel, atlantisView);
                    //gameBoardView.showOptions(atlantisModel.getLanguageList(), atlantisModel.getCurrentLanguage(), gameBoardView.getGameStage());
                }
            }
        });
    }

    private void handleUserInput() {

        for (Card movementCard : gameModel.getLocalPlayer().getMovementCards()) {

            movementCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (playerTurn == gameModel.getLocalPlayer().getPlayerID()) {
                        selectedCard = movementCard;
                    }
                    validateMove(selectedCard, selectedGamePiece);
                }
            });
        }

        for (GamePiece gamePiece : gameModel.getLocalPlayer().getGamePieces()) {

            gamePiece.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (playerTurn == gameModel.getLocalPlayer().getPlayerID()) {
                        selectedGamePiece = gamePiece;
                    }
                    validateMove(selectedCard, selectedGamePiece);
                }
            });
        }

        //**********************************GAME CONTROL BUTTONS************************************ //

        gameBoardView.getButtonBuyCards().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        gameBoardView.getButtonMove().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        gameBoardView.getButtonReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        gameBoardView.getButtonEndTurn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

    }

    private void validateMove(Card selectedCard, GamePiece selectedGamePiece) {

        if (selectedCard != null && selectedGamePiece != null) {

            try {


                sendHashMap();
                selectedCard = null;
                selectedGamePiece = null;

                //here we catch the own made exceptions
            } catch (Exception ex) {
                // Here we can inform the player abput his mistakes
            } //catch (NotMyTurnException ex) {$
            //}
        }
    }

    public void sendHashMap() {
        HashMap<String, Object> mapToSend = new HashMap<>();
        mapToSend.put("Card", selectedCard.getColorSet());
        mapToSend.put("GamePiece", selectedGamePiece);
        atlantisModel.sendMessage(new Message(MessageType.MOVE, mapToSend));

    }

    public void showGame() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameBoardView.show();
                startListeners();
                handleUserInput();
            }
        });
    }






































}
