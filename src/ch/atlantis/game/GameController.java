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
                    cardBehindPathId = selectedCard.getpathId()-1;
                }
            });
        }


        for (Player player : gameBoardView.getPlayers()) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                gamePiece.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                        if (selectedCard.getCardType() != CardType.START) {
                            gamePiece.moveGamePiece(selectedCard.getLayoutX(), selectedCard.getLayoutY());
                        }

                        for (Card card : pathCards){
                            if (card.getpathId() == cardBehindPathId && card.isOnTop()){
                                gameBoardView.removePathCard(card);
                            }
                            if (card.getpathId() == cardBehindPathId && !card.isOnTop()){
                                card.setIsOnTop(true);
                            }
                        }
                    }
                });
            }
        }




    }
}
