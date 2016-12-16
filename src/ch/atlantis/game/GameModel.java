package ch.atlantis.game;

import ch.atlantis.AtlantisClient;
import ch.atlantis.util.Message;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hermann Grieder on 28.10.2016
 */
public class GameModel {

    private SimpleIntegerProperty waterOnTheWayPathId;
    private ArrayList<Integer> playedCardsIndices;
    private ArrayList<Integer> paidCardsIndex;
    private ArrayList<Integer> targetPathIds;
    private SimpleBooleanProperty occupied;
    private ArrayList<Card> deckCardToAdd;
    private GamePiece selectedGamePiece;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCards;
    private ArrayList<Tile> tiles;
    private Card selectedCard;
    private int selectedStackCardIndex;
    private int localPlayerId;
    private int indexOfPathCardToRemove;
    private int indexOfPathCardToShow;
    private Card selectedStackCard;
    private int targetPathIdRemote;
    private int gamePieceUsedIndex;
    private int currentTurn;
    private int previousTurn;
    private int targetPathId;
    private SimpleIntegerProperty priceToCrossWater;
    private int pathIdAfter;

    private Logger logger;
    private boolean paidCorrectPrice;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);
        logger.setLevel(Level.INFO);
        localPlayerId = localPlayer.getPlayerID();
        occupied = new SimpleBooleanProperty(false);
        waterOnTheWayPathId = new SimpleIntegerProperty(0);
        priceToCrossWater = new SimpleIntegerProperty(0);
        playedCardsIndices = new ArrayList<>();
        deckCardToAdd = new ArrayList<>();
        currentTurn = 0;
        previousTurn = currentTurn;

        HashMap<String, Object> newGameStateMap = (HashMap<String, Object>) message.getMessageObject();
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
        if (!targetPathIds.contains(targetPathId)) {
            targetPathIds.add(targetPathId);
        }
        // Check if the target pathId is already occupied by someone else
        occupied.set(checkIfOccupied());
        System.out.println(occupied);
        boolean hasWater = false;
        if (!occupied.get() && !paidCorrectPrice) {
            hasWater = checkForWater();
        }
        System.out.println("Occupied: " + occupied + " hasWater: " + hasWater);
        return (!occupied.get() && !hasWater);
    }

    private boolean checkForWater() {
        // Check if there is water on the way to the target. Returns the pathId of that water tile or 0 if no
        // water is on the way to the target
        int priceToCross = 0;
        int waterPathId = getWaterPathId(selectedGamePiece.getStartPathId());
        while (waterPathId != 0) {
            priceToCross += getPriceForCrossing(waterPathId);
            waterPathId = getWaterPathId(pathIdAfter - 1);
        }
        priceToCrossWater.set(priceToCross);
        System.out.println("Price to cross " + priceToCrossWater.get());
        System.out.println("Price to cross true / false: " + (priceToCross != 0));
        return priceToCross != 0;
    }

    public boolean hasPaidCorrectPrice() {
        paidCorrectPrice = false;
        if (paidCardsIndex == null) {
            paidCardsIndex = new ArrayList<>();
        }
        if (selectedStackCard.getValue() >= priceToCrossWater.get()) {
            int index = players.get(localPlayerId).getPathCardStack().indexOf(selectedStackCard);
            paidCardsIndex.add(index);
            priceToCrossWater.set(0);
            paidCorrectPrice = true;
        }
        return paidCorrectPrice;
    }

    /**
     * Finds the target path id of where the gamePiece should ultimately end up on.
     *
     * @return int - The targetPathId
     */
    public int findTargetPathId() {
        if (targetPathIds == null) {
            targetPathIds = new ArrayList<>();
        }
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
                    logger.info(String.valueOf(targetPathId));
                }
            }
            nextPathId++;
        }
        // If we cannot find a targetPathId on the path then the next target is the end
        if (!found && nextPathId == 154) {
            targetPathId = 400;
        }
        return targetPathId;
    }

    /**
     * Checks if the targetPathId that was found is already occupied.
     *
     * @return True if the target is occupied, false if it is free to go to
     */
    private boolean checkIfOccupied() {
        for (Player player : players) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                if (gamePiece != selectedGamePiece && gamePiece.getCurrentPathId() == targetPathId && gamePiece.getCurrentPathId() != 400) {
                    logger.info("GameModel -> TargetPathID is occupied.");
                    selectedGamePiece.setCurrentPathId(targetPathId);
                    return true;
                }
            }
        }
        logger.info("GameModel -> TargetPathID is not occupied.");
        return false;
    }

    /**
     * Hermann Grieder
     * <br>
     * Recursive method that goes trough each pathCard on the way
     * to the target to check if there is water on the way
     *
     * @param currentPathId The pathId of the GamePiece to be moved
     * @return The pathId of the water tile
     */
    private int getWaterPathId(int currentPathId) {
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
                return getWaterPathId(startPathId);
            }
        }
        // If by the time we get to the target path and have not found any water tiles we return 0
        if (startPathId == targetPathId) {
            logger.info("No water found to the target.");
            return 0;
        }
        logger.info("Water found on PathID: " + startPathId);
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
            logger.info("GameModel -> Price to cross: " + valueAfter);
            return valueAfter;
        } else {
            logger.info("GameModel -> Price to cross: " + valueBehind);
            return valueBehind;
        }
    }

    /**
     * Fabian Witschi
     * <br>
     * Since we found the first water card on the way to the target card it might be that on the following
     * card it has more water cards and this method is checking if there is on the next pathId more than one card
     * if so we want to get the one at the top which is cardtype NOT water and is on top. If we get only one card
     * on the pathId we recall the method (recursive) in order to iterate through the follwing cards until we get
     * a "normal" path card.
     *
     * @param pathId
     * @return valueOfCardAfter
     */
    private int getValueFromCardAfter(int pathId) {
        int valueOfCardAfter = 0;
        pathIdAfter = pathId + 1;
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

    /**
     * Fabian Witschi
     * <br>
     * In the method ifWaterOnTheWay we check already each path card until we get to the first water card
     * therefore it is not necessary to iterate backwards until we find the next "normal" card - so just getting
     * the value of the card behind is enough for calculating the price for passing
     *
     * @param pathId
     * @return valueOfCardBehind
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
        gameStateMap.put("GamePieceIndex", players.get(localPlayerId).getGamePieces().indexOf(selectedGamePiece));
        gameStateMap.put("TargetPathIds", targetPathIds);
        if (paidCardsIndex != null) {
            logger.info("Client -> Paid card index: " + paidCardsIndex.size());
            gameStateMap.put("PaidCards", paidCardsIndex);
        }

        // Strange behaviour: When I try to send playedCardsIndices directly, a maximum of one number arrives at the
        // server. So I finally tried to create a new ArrayList and it works. I do not know why this problem exists.
        ArrayList<Integer> newPlayedCardsIndices = new ArrayList<>();
        logger.info("Played cards indices size: " + playedCardsIndices.size());
        for (Integer integer : playedCardsIndices) {
            newPlayedCardsIndices.add(integer);
        }

        // sending the new ArrayList instead of playedCardsIndices directly
        gameStateMap.put("PlayedCardsIndices", newPlayedCardsIndices);

        return gameStateMap;
    }

    /**
     * Hermann Grieder
     * <br>
     *
     * @param gameStateMap
     */
    @SuppressWarnings("unchecked")
    public void readInitialGameStateMap(HashMap<String, Object> gameStateMap) {
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        players = (ArrayList<Player>) gameStateMap.get("Players");
        tiles = (ArrayList<Tile>) gameStateMap.get("Tiles");
        pathCards = (ArrayList<Card>) gameStateMap.get("PathCards");
        logger.info("CurrentTurn: " + this.currentTurn);
    }

    /**
     * Hermann Grieder
     * <br>
     *
     * @param gameStateMap
     */
    @SuppressWarnings("unchecked")
    public boolean readGameStateMap(HashMap<String, Object> gameStateMap) {
        previousTurn = currentTurn;
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        players = (ArrayList<Player>) gameStateMap.get("Players");
        int score = (int) gameStateMap.get("Score");
        players.get(previousTurn).setScore(score);
        gamePieceUsedIndex = (int) gameStateMap.get("GamePieceUsedIndex");
        targetPathIdRemote = (int) gameStateMap.get("TargetPathId");
        indexOfPathCardToRemove = (int) gameStateMap.get("IndexOfCardToRemove");
        indexOfPathCardToShow = (int) gameStateMap.get("IndexOfCardToShow");
        deckCardToAdd = (ArrayList<Card>) gameStateMap.get("DeckCardsToAdd");
        return true;
    }

    /**
     * Hermann Grieder
     * <br>
     */

    public boolean updateValues() {
        players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex).setCurrentPathId(targetPathIdRemote);


//        int score = pathCards.get(indexOfPathCardToRemove).getValue();
//        if (previousTurn == localPlayerId) {
//            scoreList.add(String.valueOf(score));
//        }

        Card pathCardToRemove = pathCards.get(indexOfPathCardToRemove);
        pathCardToRemove.setPathId(-1);
        players.get(previousTurn).getPathCardStack().add(pathCardToRemove);
        removePaidCardsFromStack();
        pathCards.get(indexOfPathCardToShow).setIsOnTop(true);
        selectedGamePiece = players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex);
        selectedGamePiece.setCurrentPathId(targetPathIdRemote);
        updateMovementCards();
        return true;

    }

    private void removePaidCardsFromStack() {
        if (!(paidCardsIndex == null || paidCardsIndex.size() == 0)) {
            ArrayList<Card> stacksCardsToRemove = new ArrayList<>();
            for (Integer index : paidCardsIndex) {
                stacksCardsToRemove.add(players.get(previousTurn).getPathCardStack().get(index));
            }

            for (Card card : stacksCardsToRemove) {
                players.get(previousTurn).getPathCardStack().remove(card);
            }
        }
    }


    private void updateMovementCards() {
        ArrayList<Card> movementCardsToRemove = new ArrayList<>();
        for (Integer index : playedCardsIndices) {
            movementCardsToRemove.add(players.get(previousTurn).getMovementCards().get(index));
        }

        for (Card card : movementCardsToRemove) {
            players.get(previousTurn).getMovementCards().remove(card);
        }

        for (Card card : deckCardToAdd) {
            players.get(previousTurn).getMovementCards().add(card);
        }
    }

    // ********************************* GETTERS & SETTERS ***************************** //

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
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

    public ArrayList<Integer> getPlayedCardsIndices() {
        return playedCardsIndices;
    }

    public GamePiece getSelectedGamePiece() {
        return selectedGamePiece;
    }

    public int getGamePieceUsedIndex() {
        return gamePieceUsedIndex;
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

    public SimpleBooleanProperty occupiedProperty() {
        return occupied;
    }

    public SimpleIntegerProperty priceToCrossWaterProperty() {
        return priceToCrossWater;
    }

    public void addToPlayedCards() {
        int index = players.get(localPlayerId).getMovementCards().indexOf(selectedCard);
        playedCardsIndices.add(index);
    }

    public void setTargetPathIds(ArrayList<Integer> targetPathIds) {
        this.targetPathIds = targetPathIds;
    }

    public int getSelectedStackCardIndex() {
        return selectedStackCardIndex;
    }

    public void setSelectedStackCardIndex(int selectedStackCardIndex) {
        this.selectedStackCardIndex = selectedStackCardIndex;
    }

    public Card getSelectedStackCard() {
        return selectedStackCard;
    }

    public void setSelectedStackCard(Card selectedStackCard) {
        this.selectedStackCard = selectedStackCard;
    }

    public void clearPaidCardsIndex() {
        paidCardsIndex = null;
    }

    public String getWinnerName() {
        String winner = null;
        int score = 0;
        for (Player player : players) {
            if (player.getScore() > score) {
                score = player.getScore();
                winner = player.getPlayerName();
            }
            if (player.getScore() == score) {
                winner = null;
            }
        }
        return winner;
    }

    public void setPaidCorrectPrice(boolean paidCorrectPrice) {
        this.paidCorrectPrice = paidCorrectPrice;
    }
}
