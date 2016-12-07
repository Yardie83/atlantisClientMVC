package ch.atlantis.game;

import ch.atlantis.util.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hermann Grieder on 28.10.2016
 */
public class GameModel {

    private ArrayList<Tile> tiles;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCards;
    private Player localPlayer;
    private int currentTurn;
    private int previousTurn;
    private Card selectedCard;
    private GamePiece selectedGamePiece;
    private int cardPlayedIndex;
    private int gamePieceUsedIndex;
    private HashMap<String, Object> gameStateMap;
    private HashMap<String, Object> previousGameStateMap;
    private int targetPathId;
    private int targetFoundInTurn;
    private int targetPathIdRemote;
    private int indexOfPathCardToRemove;
    private int indexOfPathCardToShow;
    private Card newCardFromDeck;
    private int indexOfSelectedCard;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        this.gameStateMap = (HashMap<String, Object>) message.getMessageObject();
        this.previousGameStateMap = gameStateMap;
        this.targetFoundInTurn = -1;
        this.currentTurn = 0;
        this.previousTurn = this.currentTurn;
        readInitialGameStateMap(gameStateMap);
        initLocalPlayer(localPlayer);
        applyPlayerColor();

    }

    private void initLocalPlayer(Player localPlayer) {
        for (Player player : players) {
            if (player.getPlayerID() == localPlayer.getPlayerID()) {
                this.localPlayer = player;
            }
        }
    }

    private void applyPlayerColor() {
        for (Player player : players) {
            player.applyColor();
        }
    }


    public boolean buyHandCard(int value) {


        return false;
    }

    public int findTargetPathId() {

        int startPathId = 101;
        if (selectedGamePiece.getCurrentPathId() != 300) {
            startPathId = selectedGamePiece.getCurrentPathId() + 1;
        }

        boolean found = false;
        int nextPathId = startPathId;
        targetPathId = 0;
        while (!found && nextPathId < 154) {
            for (Card pathCard : pathCards) {
                if (pathCard.isOnTop()
                        && pathCard.getCardType() != CardType.WATER
                        && pathCard.getPathId() == nextPathId
                        && pathCard.getColorSet() == selectedCard.getColorSet()) {
                    found = true;
                    targetPathId = pathCard.getPathId();
                    System.out.println(targetPathId);
                }
            }
            nextPathId++;
        }
        // If we cannot find a targetPathId on the path then the next target is the end
        if (!found && nextPathId + 1 == 154) {
            targetPathId = 400;
        }
        targetFoundInTurn = currentTurn;
        return targetPathId;
    }

    /**
     * Fabian Witschi
     * <br>
     * <p>
     * Finds the price that needs to be paid to cross one or more water tiles
     *
     * @param pathId The current pathId of the gamePiece that was moved there
     * @return The price to cross
     */
    private int getPriceForCrossing(int pathId) {
        int pathIdBehind = pathId - 1;
        int pathIdAfter = pathId + 1;
        int valueBehind = 0;
        int valueAfter = 0;

        for (Card pathCard : pathCards) {

            if (pathIdBehind >= 101 && pathIdAfter <= 154) {

                if (pathCard.getCardType() != CardType.WATER) {

                    if (pathCard.getPathId() == pathIdBehind) {
                        valueBehind = pathCard.getValue();
                    }
                    if (pathCard.getPathId() == pathIdAfter) {
                        valueAfter = pathCard.getValue();
                    }
                } else {
                    if (pathCard.getPathId() == pathIdBehind) {
                        getPriceForCrossing(pathIdBehind--);
                    }
                    if (pathCard.getPathId() == pathIdAfter) {
                        getPriceForCrossing(pathIdAfter++);
                    }
                }
            }
        }
        if (valueBehind > valueAfter) {
            return valueAfter;
        } else {
            return valueBehind;
        }
    }

    public HashMap<String, Object> writeGameStateMap() {
        HashMap<String, Object> gameStateMap = new HashMap<>();
        gameStateMap.put("CurrentTurn", currentTurn);
        gameStateMap.put("PlayerId", localPlayer.getPlayerID());
        gameStateMap.put("GameName", localPlayer.getGameName());
        indexOfSelectedCard = players.get(localPlayer.getPlayerID()).getMovementCards().indexOf(selectedCard);
        gameStateMap.put("SelectedCard", indexOfSelectedCard);
        gameStateMap.put("GamePieceIndex", localPlayer.getGamePieces().indexOf(selectedGamePiece));
        gameStateMap.put("TargetPathId", targetPathId);
        return gameStateMap;
    }

    @SuppressWarnings("unchecked")
    public void readInitialGameStateMap(HashMap<String, Object> gameStateMap) {
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        players = (ArrayList<Player>) gameStateMap.get("Players");
        tiles = (ArrayList<Tile>) gameStateMap.get("Tiles");
        pathCards = (ArrayList<Card>) gameStateMap.get("PathCards");
        System.out.println("CurrentTurn: " + this.currentTurn);
    }

    @SuppressWarnings("unchecked")
    public void readGameStateMap(HashMap<String, Object> gameStateMap) {
        previousTurn = currentTurn;
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        players = (ArrayList<Player>) gameStateMap.get("Players");
        cardPlayedIndex = (int) gameStateMap.get("SelectedCard");
        gamePieceUsedIndex = (int) gameStateMap.get("GamePieceUsedIndex");
        targetPathIdRemote = (int) gameStateMap.get("TargetPathId");
        indexOfPathCardToRemove = (int) gameStateMap.get("IndexOfCardToRemove");
        indexOfPathCardToShow = (int) gameStateMap.get("IndexOfCardToShow");
        newCardFromDeck = (Card) gameStateMap.get("DeckCard");
    }

    public void updateValues() {
        players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex).setCurrentPathId(targetPathIdRemote);
        players.get(previousTurn).getMovementCards().remove(cardPlayedIndex);
        players.get(previousTurn).getMovementCards().add(newCardFromDeck);
        players.get(previousTurn).addScore(pathCards.get(indexOfPathCardToRemove).getValue());
        pathCards.get(indexOfPathCardToRemove).setPathId(-1);
        pathCards.get(indexOfPathCardToShow).setIsOnTop(true);
        localPlayer = players.get(localPlayer.getPlayerID());
        System.out.println(localPlayer.getScore() + " <- Local Score : Remote Score -> " + players.get(previousTurn).getScore());
    }

    @SuppressWarnings("unchecked")
    public void reloadGameStateMap(HashMap<String, Object> gameStateMap) {
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        selectedCard = (Card) gameStateMap.get("Card");
        selectedGamePiece = (GamePiece) gameStateMap.get("GamePiece");
        System.out.println("CurrentTurn: " + this.currentTurn);
    }

    // ********************************* GETTERS & SETTERS ***************************** //

    public GamePiece getGamePieceToMove() {
        return players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getPreviousTurn() {
        return previousTurn;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    public GamePiece getSelectedGamePiece() {
        return selectedGamePiece;
    }

    public void setSelectedGamePiece(GamePiece selectedGamePiece) {
        this.selectedGamePiece = selectedGamePiece;
    }

    public int getIndexOfPathCardToRemove() {
        return indexOfPathCardToRemove;
    }

    public int getIndexOfPathCardToShow() {
        return indexOfPathCardToShow;
    }

    public int getCardPlayedIndex() {
        return cardPlayedIndex;
    }

    public Card getNewCardFromDeck() {
        return newCardFromDeck;
    }

    public HashMap<String, Object> getPreviousGameStateMap() {
        return previousGameStateMap;
    }

    public void savePreviousGameStateMap() {
        if (targetFoundInTurn != currentTurn) findTargetPathId();
        this.previousGameStateMap = writeGameStateMap();
    }


}
