package ch.atlantis.game;

import ch.atlantis.util.Message;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hermann Grieder on 28.10.2016
 */
public class GameModel {

    private HashMap<String, Object> currentGameStateMap;
    private SimpleBooleanProperty targetNotOccupied;
    private HashMap<String, Object> newGameStateMap;
    private ArrayList<Card> selectedCards;
    private GamePiece selectedGamePiece;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCards;
    private ArrayList<Tile> tiles;
    private Card newCardFromDeck;
    private ArrayList<Card> deck;
    private int localPlayerId;
    private Card selectedCard;
    private int indexOfPathCardToRemove;
    private int indexOfPathCardToShow;
    private int indexOfSelectedCard;
    private int targetPathIdRemote;
    private int gamePieceUsedIndex;
    private int targetFoundInTurn;
    private int cardPlayedIndex;
    private int currentTurn;
    private int previousTurn;
    private int targetPathId;
    private SimpleIntegerProperty waterOnTheWayPathId;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {
        localPlayerId = localPlayer.getPlayerID();
        targetNotOccupied = new SimpleBooleanProperty(true);
        waterOnTheWayPathId = new SimpleIntegerProperty(0);
        selectedCards = new ArrayList<>();
        newGameStateMap = (HashMap<String, Object>) message.getMessageObject();
        currentGameStateMap = newGameStateMap;
        targetFoundInTurn = -1;
        currentTurn = 0;
        previousTurn = currentTurn;

        readInitialGameStateMap(newGameStateMap);
        applyPlayerColor();

    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the color values according to the player id's. So player 1 will always be red for each client, etc.
     */
    private void applyPlayerColor() {
        for (Player player : players) {
            player.applyColor();
        }
    }

    /**
     * Finds the target path id of where the gamePiece should ultimately end up on.
     * @return int - The targetPathId
     */
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
     * Checks if the targetPathId that was found is already occupied.
     *
     * @param targetPathId    The pathId the gamePiece should be moved to
     * @param selectedGamePiece The gamePiece that was moved
     * @return True if the target is occupied, false if it is free to go to
     */
    private boolean checkIfOccupied(int targetPathId, GamePiece selectedGamePiece) {
        for (Player player : players) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                if (gamePiece != selectedGamePiece && gamePiece.getCurrentPathId() == targetPathId) {
                    System.out.println("GameModel -> TargetPathId is occupied");
                    return false;
                }
            }
        }
        System.out.println("GameModel -> TargetPathId is not occupied");
        return true;
    }

    /**
     * Hermann Grieder
     * <br>
     * Recursive method that goes trough each pathCard on the way
     * to the target to check if there is water on the way
     *
     * @param currentPathId The pathId of the GamePiece to be moved
     * @param targetPathId  The targetPathId where the GamePiece ultimately should be
     * @return The pathId of the water tile
     */
    private int checkIfWaterOnTheWay(int currentPathId, int targetPathId) {
        int startPathId = currentPathId + 1;

        // If the gamePiece is on the home tile we need to check from the first actual path tile
        if (currentPathId == 300) {
            startPathId = 101;
        }
        // If our target would be the end tile we have to check up to the last path card on the way
        int target = targetPathId;
        if (target == 400) {
            target = 153;
        }
        int count = 0;
        // We count the cards that have the same pathId as the startPathId. If we count more than one
        // card it must be an exposed water tile.
        if (startPathId < target) {
            for (Card pathCard : pathCards) {
                if (pathCard.getPathId() == startPathId) {
                    count++;
                }
            }
            // Recursive call in case we find more than one card on that pathId.
            if (count != 1) {
                System.out.println("GameModel - > There are " + count + " cards at " + startPathId);
                return checkIfWaterOnTheWay(startPathId, targetPathId);
            }
        }
        // If by the time we get to the target path and have not found any water tiles we return 0
        if (startPathId == targetPathId) {
            System.out.println("No water found to the target");
            return 0;
        }
        System.out.println("Water on PathId: " + startPathId);
        return startPathId;
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
        int valueBehind = getValueFromCardBehind(pathId);
        int valueAfter = getValueFromCardAfter(pathId);
        if (valueBehind > valueAfter) {
            System.out.println("GameModel -> Price to cross: " + valueAfter);
            return valueAfter;
        } else {
            System.out.println("GameModel -> Price to cross: " + valueBehind);
            return valueBehind;
        }
    }

    /** Fabian Witschi
     *  <br>
     *  Since we found the first water card on the way to the target card it might be that on the following
     *  card it has more water cards and this method is checking if there is on the next pathId more than one card
     *  if so we want to get the one at the top which is cardtype NOT water and is on top. If we get only one card
     *  on the pathId we recall the method (recursive) in order to iterate through the follwing cards until we get
     *  a "normal" path card.
     *
     * @param pathId
     * @return valueOfCardAfter
     */
    private int getValueFromCardAfter(int pathId) {
        int valueOfCardAfter = 0;
        int pathIdAfter = pathId + 1;
        ArrayList<Card> tempList = new ArrayList<>();

        if (pathIdAfter < 154) {
            for (Card pathCard : pathCards) {
                if (pathCard.getPathId() == pathIdAfter) {
                    tempList.add(pathCard);
                }
            }
            if (tempList.size() > 1) {
                for (Card pathCard : tempList) {
                    if (pathCard.getCardType() != CardType.WATER && pathCard.isOnTop()) {
                        valueOfCardAfter = pathCard.getValue();
                    }
                }
            } else {
                getValueFromCardAfter(pathIdAfter);
            }
        }

        return valueOfCardAfter;
    }

    /** Fabian Witschi
     *  <br>
     *  In the method ifWaterOnTheWay we check already each path card until we get to the first water card
     *  therefore it is not necessary to iterate backwards until we find the next "normal" card - so just getting
     *  the value of the card behind is enough for calculating the price for passing
     *  @param pathId
     *  @return valueOfCardBehind
     */
    private int getValueFromCardBehind(int pathId) {
        int pathIdBehind = pathId - 1;
        int valueOfCardBehind = 0;
        for (Card pathCard : pathCards) {
            if (pathCard.getPathId() == pathIdBehind) {
                if (pathCard.getCardType() != CardType.WATER && pathCard.isOnTop()) {
                    valueOfCardBehind = pathCard.getValue();
                }
            }
        }
        return valueOfCardBehind;
    }

    /**
     * Hermann Grieder
     * <br>
     *
     * @return
     */
    public HashMap<String, Object> writeGameStateMap() {
        HashMap<String, Object> gameStateMap = new HashMap<>();
        gameStateMap.put("CurrentTurn", currentTurn);
        gameStateMap.put("PlayerId", players.get(currentTurn).getPlayerID());
        gameStateMap.put("GameName", players.get(currentTurn).getGameName());
        indexOfSelectedCard = players.get(localPlayerId).getMovementCards().indexOf(selectedCard);
        gameStateMap.put("SelectedCard", indexOfSelectedCard);
        gameStateMap.put("GamePieceIndex", players.get(localPlayerId).getGamePieces().indexOf(selectedGamePiece));
        gameStateMap.put("TargetPathId", targetPathId);
        return gameStateMap;
    }

    /**
     * Hermann Grieder
     * <br>
     * @param gameStateMap
     */
    @SuppressWarnings("unchecked")
    public void readInitialGameStateMap(HashMap<String, Object> gameStateMap) {
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        players = (ArrayList<Player>) gameStateMap.get("Players");
        tiles = (ArrayList<Tile>) gameStateMap.get("Tiles");
        pathCards = (ArrayList<Card>) gameStateMap.get("PathCards");
        deck = (ArrayList<Card>) gameStateMap.get("Deck");
        System.out.println("CurrentTurn: " + this.currentTurn);
    }

    /**
     * Hermann Grieder
     * <br>
     *
     * @param gameStateMap
     */
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
        System.out.println(newCardFromDeck);
    }

    /**
     * Hermann Grieder
     * <br>
     */
    public boolean updateValues() {
        players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex).setCurrentPathId(targetPathIdRemote);
        players.get(previousTurn).getMovementCards().remove(cardPlayedIndex);
        players.get(previousTurn).getMovementCards().add(newCardFromDeck);
        players.get(previousTurn).addScore(pathCards.get(indexOfPathCardToRemove).getValue());
        pathCards.get(indexOfPathCardToRemove).setPathId(-1);
        pathCards.get(indexOfPathCardToShow).setIsOnTop(true);
        selectedGamePiece = players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex);
        selectedCard = players.get(previousTurn).getMovementCards().get(cardPlayedIndex);
        selectedGamePiece.setCurrentPathId(targetPathIdRemote);
        selectedCards.clear();
        return true;
    }

    public void saveCurrentGameState() {
        if (targetFoundInTurn != currentTurn) findTargetPathId();
        currentGameStateMap = writeGameStateMap();

        for (GamePiece gamePiece : players.get(localPlayerId).getGamePieces() ){
            gamePiece.setStartPathId(gamePiece.getCurrentPathId());
        }
    }

    // ****************************** HANDLE MOVE **************************************//

    /**
     * Hermann Grieder
     * <br>
     *
     * @return
     */
    public boolean canMoveDirectly() {

        // Find the target pathId on the client side
        targetPathId = findTargetPathId();
        selectedGamePiece.setTargetPathId(targetPathId);
        // Check if the target pathId is already occupied by someone else
        targetNotOccupied.setValue(checkIfOccupied(targetPathId, selectedGamePiece));

        // Check if there is water on the way to the target. Returns the pathId of that water tile or 0 if not water is on the way

        waterOnTheWayPathId.set(checkIfWaterOnTheWay(selectedGamePiece.getCurrentPathId(), targetPathId));

        // If there is water on the way to the target then calculate the price to cross
        int priceToCrossWater = 0;
        if (waterOnTheWayPathId.get() != 0) {
            priceToCrossWater = getPriceForCrossing(waterOnTheWayPathId.get());
        }

        System.out.println("GameModel -> Move can be done directly");
        return targetNotOccupied.getValue() && waterOnTheWayPathId.get() == 0;
    }


//        // Remove the movement card played by the player and add it to the discarded cards list
//        Card cardToDiscard = players.get(activePlayerId).getMovementCards().get(selectedCard);
//        players.get(activePlayerId).getMovementCards().remove(selectedCard);
//        discardedCards.add(cardToDiscard);
//        System.out.println("GameModel -> Movement card removed");
//        System.out.println("GameModel -> Player holds " + players.get(activePlayerId).getMovementCards().size() + " cards");
//
//        // Pick up the card behind the gamePiece
//        int scoreToAdd = removePathCardFromPath(targetPathId);
//        // TODO: We need a list for the individuals score picked up by the player. So we can later pay with it.
//        // Add the score of that card to the player
//        players.get(activePlayerId).addScore(scoreToAdd);
//        System.out.println("GameModel -> Score of " + scoreToAdd + " added to " + players.get(activePlayerId).getPlayerName());
//
//        // Give the player new movement cards. The amount of cards the player played, plus for each GamePiece
//        // that has reached the end, one additional card
//        addCardFromDeckToPlayer();
//        System.out.println("GameModel -> Player holds " + players.get(activePlayerId).getMovementCards().size() + " cards");
//
//        // Increase the turn count
//        currentTurnLocal++;
//        if (currentTurnLocal >= players.size()) {
//            currentTurnLocal = 0;
//        }
//        System.out.println("GameModel -> PlayerTurn: " + this.currentTurnLocal);


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

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public int getLocalPlayerId() {
        return localPlayerId;
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

    public int getTargetPathId() {
        return targetPathId;
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

    public HashMap<String, Object> getCurrentGameStateMap() {
        return currentGameStateMap;
    }

    public SimpleBooleanProperty targetNotOccupiedProperty() {
        return targetNotOccupied;
    }

    public SimpleIntegerProperty waterOnTheWayPathIdProperty() {
        return waterOnTheWayPathId;
    }
}
