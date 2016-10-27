package ch.atlantis.game;

import ch.atlantis.controller.OptionsController;
import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */
public class GameController {


    private GameBoardView gameBoardView;
    private AtlantisView atlantisView;
    private GameModel gameModel;
    private AtlantisModel atlantisModel;
    private Card selectedCard;
    private Card cardToMove;
    private int cardBehindPathId;
    private int cardToMoveId;
    private int tempColorSet;
    private ArrayList<Card> pathCards;
    private ArrayList<Card> movementCards;
    private int playerId;
    private int turnId = 0;
    private GamePiece tempoGamePiece;
    private Deck deck;

    public GameController( AtlantisView atlantisView, AtlantisModel atlantisModel, GameModel gameModel, GameBoardView gameBoardView ) {
       this.atlantisView = atlantisView;
        this.atlantisModel = atlantisModel;
        this.gameModel = gameModel;
        this.gameBoardView = gameBoardView;
        gameBoardView.show();
        handleUserInput();
        addListeners();
    }


    private void addListeners() {
        gameBoardView.getGameStage().getScene().setOnKeyPressed( new EventHandler<KeyEvent>() {
            @Override
            public void handle( KeyEvent event ) {
                if ( event.getCode() == KeyCode.ESCAPE ) {
                    gameBoardView.showOptions( atlantisModel.getLanguageList(), atlantisModel.getCurrentLanguage(), gameBoardView
                            .getGameStage() );
                    new OptionsController( atlantisModel, atlantisView );
                }
            }
        });
    }

    private void handleUserInput() {

        movementCards = gameBoardView.getPlayers().get(turnId).getHandCards();

        for (Card card : movementCards) {
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    tempColorSet = card.getColorSet();
                    cardToMove = possiblePathCard(card);
                    card.setIsPlayedTrue();
                    cardBehindPathId = cardToMove.getPathId() - 1;
                    cardToMoveId = cardToMove.getPathId();
                }
            });
        }

        for (Player player : gameBoardView.getPlayers()) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                gamePiece.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // Place the player game piece in the middle of the card that corresponds
                        // with the card that was played
                        cardToMove = possiblePathCard(gamePiece);
                        if (cardToMove.getCardType() != CardType.START) {
                            if (isOccupied(cardToMove)) {
                                while (isOccupied(cardToMove)) {
                                    cardToMove = getNextCard();
                                }
                            } else if (myTurn(gamePiece)) {
                                gamePiece.moveGamePiece(cardToMove.getLayoutX() + (cardToMove.getWidth() / 2) - (gamePiece.getWidth() / 2),
                                        cardToMove.getLayoutY() + (cardToMove.getHeight() / 2) - (gamePiece.getHeight() / 2));
                                gamePiece.setGamePiecePathId(cardToMoveId);
                            }

                        }
                        // Find the card behind the player which is on top
                        // and remove it, but leave the card if it is already a water card
                        /*for (Card card : pathCards ) {
                            if (isOccupied(card)) {
                                while (isOccupied(card)) {
                                    cardBehindPathId--;
                                }
                            } else {
                            if (card.getPathId() == cardBehindPathId && card.isOnTop() && card.getCardType() !=
                            CardType.WATER) {
                                gameBoardView.removePathCard(card);
                            }
                            if (card.getPathId() == cardBehindPathId && !card.isOnTop() && card.getCardType() !=
                            CardType.WATER) {
                                card.setIsOnTop(true);
                            }
                        }
                        } */
                    }
                });
            }

        }
    }

    private Card possiblePathCard(Card handCard) {

        for (int i = 101; i < 154; i++) {
            for (Card pathCard : gameBoardView.getPathCards()) {
                if (pathCard.getPathId() == i) {
                    if (pathCard.getColorSet() == handCard.getColorSet()) {
                        if (pathCard.isOnTop() && pathCard.getCardType() != CardType.WATER
                                && pathCard.getCardType() != CardType.START) {
                            cardBehindPathId = i - 1;
                            return pathCard;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Card possiblePathCard(GamePiece gamePiece) {

        for (int i = gamePiece.getGamePiecePathId(); i < 154; i++) {
            for (Card pathCard : gameBoardView.getPathCards()) {
                if (pathCard.getPathId() == i) {
                    if (pathCard.getColorSet() == tempColorSet) {
                        if (pathCard.isOnTop() && pathCard.getCardType() != CardType.WATER
                                && pathCard.getCardType() != CardType.START) {
                            cardBehindPathId = i - 1;
                            return pathCard;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean myTurn(GamePiece gamePiece) {

        if (turnId == gameBoardView.getPlayers().size()) {
            turnId = 0;
        }

        if (turnId == gamePiece.getPlayerId()) {
            turnId++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fabian Witschi
     *
     * @return
     */
    private Card getNextCard() {
        for (Card card : gameBoardView.getPathCards()) {
            if (card.getPathId() == cardToMove.getPathId() + 1) {
                return card;
            }
        }
        return null;
    }

    private boolean isOccupied(Card cardToMove) {

        for (Player player : gameBoardView.getPlayers()) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                if (gamePiece.getGamePieceX() == cardToMove.getLayoutX() + (cardToMove.getWidth() / 2) - (gamePiece.getWidth() / 2) && gamePiece.getGamePieceY() ==
                        cardToMove.getLayoutY() + (cardToMove.getHeight() / 2) - (gamePiece.getHeight() / 2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getTurnId() {
        return turnId;
    }

}
