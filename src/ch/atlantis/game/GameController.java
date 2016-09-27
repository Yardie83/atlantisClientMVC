package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */
public class GameController {


    private GameBoardView gameBoardView;

    private GameModel gameModel;
    private AtlantisModel model;
    private Card selectedCard;
    private int cardBehindPathId;

    public GameController(GameModel gameModel, AtlantisModel model, GameBoardView gameBoardView) {
        this.gameModel = gameModel;
        this.model = model;
        this.gameBoardView = gameBoardView;
        gameBoardView.show();
        handleUserInput();
    }

    private void handleUserInput() {

        ArrayList<Card> pathCards = gameBoardView.getPathCards();

        for (Card card : pathCards) {
            if (card.getCardType() == CardType.START){
                selectedCard = card;
            }
            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    selectedCard = card;
                    cardBehindPathId = selectedCard.getPathId()-1;
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
                        if (selectedCard.getCardType() != CardType.START) {
                            gamePiece.moveGamePiece(selectedCard.getLayoutX() + (selectedCard.getWidth()/2) - (gamePiece.getWidth()/2),
                                    selectedCard.getLayoutY() + (selectedCard.getHeight() /2) - (gamePiece.getHeight()/2));
                        }
                        // Find the card behind the player which is on top
                        // and remove it, but leave the card if it is already a water card
                        for (Card card : pathCards){
                            if (card.getPathId() == cardBehindPathId && card.isOnTop() && card.getCardType() != CardType.WATER){
                                gameBoardView.removePathCard(card);
                            }
                            if (card.getPathId() == cardBehindPathId && !card.isOnTop() && card.getCardType() != CardType.WATER){
                                card.setIsOnTop(true);
                            }
                        }
                    }
                });
            }
        }

    }
}
