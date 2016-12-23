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
 * Created by Hermann Grieder and Fabian Witschi on 28.10.2016
 * <p>
 * The game model is the core class before and during the game.
 * It hold all the information about the game state. Checks if a move is valid, if the right price for water crossing
 * has been paid, if a target path is occupied by another game piece and much more.
 */
public class GameModel {

    private SimpleIntegerProperty priceToCrossWater;
    private SimpleIntegerProperty priceToCrossWaterAutomatically;
    private boolean cantMoveButtonHasBeenPressed;
    private ArrayList<Integer> playedCardsIndices;
    private ArrayList<Integer> paidCardIndices;
    private ArrayList<Integer> targetPathIds;
    private SimpleBooleanProperty occupied;
    private ArrayList<Card> deckCardToAdd;
    private GamePiece selectedGamePiece;
    private ArrayList<Player> players;
    private ArrayList<Card> pathCards;
    private ArrayList<Tile> tiles;
    private Card selectedStackCard;
    private Card selectedCard;
    private int selectedStackCardIndex;
    private int indexOfPathCardToRemove;
    private int indexOfPathCardToShow;
    private boolean paidCorrectPrice;
    private int targetPathIdRemote;
    private int gamePieceUsedIndex;
    private int localPlayerId;
    private int currentTurn;
    private int previousTurn;
    private int targetPathId;
    private int pathIdAfter;

    private Logger logger;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);
        logger.setLevel(Level.INFO);
        localPlayerId = localPlayer.getPlayerID();
        occupied = new SimpleBooleanProperty(false);
        priceToCrossWater = new SimpleIntegerProperty(0);
        priceToCrossWaterAutomatically = new SimpleIntegerProperty(0);
        playedCardsIndices = new ArrayList<>();
        deckCardToAdd = new ArrayList<>();
        paidCardIndices = new ArrayList<>();
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
     * Is called after the move button, the pay button or the can't move button is clicked in the game board view.
     * It checks if the move can be done directly without any other interaction of the user. This method is called
     * as many times as there are unresolved obstacles in the way.
     *
     * @return False when the target is occupied or there is water in the way. True if the way to the target is free
     * of obstacles.
     */
    public boolean canMoveDirectly() {

        // Find the target pathId on the client side
        targetPathId = findTargetPathId();
        // When multiple movement cards are played during one move, we have to keep track of them.
        if (!targetPathIds.contains(targetPathId)) {
            targetPathIds.add(targetPathId);
        }
        // Check if the target pathId is already occupied by someone else
        occupied.set(checkIfOccupied());
        logger.info(String.valueOf(occupied));
        // If the target is not occupied, check if we have to cross water to the target or if we have now paid
        // the price calculated in an early rundown of this method.
        boolean hasWater = false;
        if (!occupied.get() && !paidCorrectPrice) {
            hasWater = checkForWater();
        }
        // If the target is not occupied and there is no water in the way to the target, return true, else false.
        logger.info("Occupied: " + occupied + "| hasWater: " + hasWater);
        return (!occupied.get() && !hasWater);
    }

    /**
     * Fabian Witschi & Hermann Grieder
     * This method is checks if there is water on the path. If we get water the method will add the lower value to the sum
     * and finally set the price that we have to pay in order to cross the water.
     *
     * @return True if there is water on the way to the target, false if there is no water
     */
    private boolean checkForWater() {
        // Check if there is water on the way to the target. Returns the pathId of that water tile or 0 if no
        // water is on the way to the target
        int waterPathId = findWaterPathId(selectedGamePiece.getStartPathId());

        // If water was found before the end, find the price to cross
        int priceToCross = 0;
        // This section deals with the rule that a player can pay for multiple water crossings with one payment
        // instead of individual payments for each crossing.
        // We add the price of a crossing and then find the start of the next water crossing
        // and add that price to the total and so on.
        while (waterPathId != 0 && getValueFromCardAfterWater(waterPathId) != 0) {
            priceToCross += getPriceForCrossing(waterPathId);
            waterPathId = findWaterPathId(pathIdAfter);
        }

        if (cantMoveButtonHasBeenPressed) {
            priceToCrossWaterAutomatically.set(priceToCross);
        } else {
            priceToCrossWater.set(priceToCross);
        }
        logger.info("Price to cross " + priceToCrossWater.get());

        return priceToCross != 0;
    }

    /**
     * Hermann Grieder
     * <br>
     * Recursive method that goes trough each pathCard on the way
     * to the target trying to find water on the way to the target
     *
     * @param currentPathId The pathId of the GamePiece to be moved
     * @return The pathId of the water tile if one was found, else it returns 0
     */
    private int findWaterPathId(int currentPathId) {
        int startPathId = currentPathId;

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
                return findWaterPathId(startPathId + 1);
            }
        }
        // If by the time we get to the target path or land tile and have not found any water tiles we return 0
        if (startPathId >= targetPathId || startPathId >= 153) {
            logger.info("No water found to the target.");
            return 0;
        }
        logger.info("Water found on PathID: " + startPathId);
        return startPathId;
    }

    /**
     * Hermann Grieder
     *
     * @return
     */
    public boolean hasPaidCorrectPrice() {
        paidCorrectPrice = false;

        int sumValuePaidCards = 0;
        for (int index : paidCardIndices) {
            sumValuePaidCards += players.get(localPlayerId).getPathCardStack().get(index).getValue();
        }
        if (sumValuePaidCards >= priceToCrossWater.get()) {
            priceToCrossWater.set(0);
            paidCorrectPrice = true;
        }
        return paidCorrectPrice;
    }

    /**
     * Hermann Grieder
     * <p>
     * Finds the target pathId where the gamePiece should ultimately end up on.
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
        logger.info("Target path id is : " + targetPathId);
        return targetPathId;
    }

    /**
     * Hermann Grieder
     * <p>
     * Checks if the targetPathId that was found is already occupied.
     *
     * @return True if the target is occupied, false if not occupied
     */
    private boolean checkIfOccupied() {
        for (Player player : players) {
            for (GamePiece gamePiece : player.getGamePieces()) {
                if (gamePiece != selectedGamePiece && gamePiece.getCurrentPathId() == targetPathId && gamePiece.getCurrentPathId() != 400) {
                    logger.info("GameModel -> TargetPathID is occupied.");
                    selectedGamePiece.setCurrentPathId(targetPathId);
                    addToPlayedCards();
                    return true;
                }
            }
        }
        logger.info("GameModel -> TargetPathID is not occupied.");
        return false;
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
        int valueBehind = getValueFromCardBehindWater(pathId);
        int valueAfter = getValueFromCardAfterWater(pathId);
        if (valueBehind > valueAfter) {
            logger.info("GameModel -> Price to cross: (Value After) " + valueAfter);
            return valueAfter;
        } else {
            logger.info("GameModel -> Price to cross: (Value Behind) " + valueBehind);
            return valueBehind;
        }
    }

    /**
     * Fabian Witschi
     * <br>
     * Since we found the first water card on the way to the target it might be that on the following
     * card it has more water cards and this method is checking if there is on the next pathId more than one card
     * if so we want to get the one at the top which is cardType NOT water and is on top. If we get only one card
     * on the pathId we recall the method (recursive) in order to iterate through the following cards until we get
     * a "normal" path card.
     *
     * @param pathId
     * @return valueOfCardAfter
     */
    private int getValueFromCardAfterWater(int pathId) {
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
                        System.out.println("LAND FOUND - > " + pathCard.getValue());
                        return pathCard.getValue();
                    }
                }
            } else {
                System.out.println("MORE WATER ON - > " + pathIdAfter);
                return getValueFromCardAfterWater(pathIdAfter);
            }
        }
        System.out.println("PRICE AFTER - > " + valueOfCardAfter);
        return valueOfCardAfter;
    }

    /**
     * Fabian Witschi
     * <br>
     * In the method findWaterPathId we found the first water card therefore it is not necessary
     * to iterate backwards until we find the next "normal" card - so just getting the value of the
     * card behind is enough for calculating the price for passing
     *
     * @param pathId
     * @return valueOfCardBehind
     */
    private int getValueFromCardBehindWater(int pathId) {
        int pathIdBehind = pathId - 1;
        int valueOfCardBehind = 0;
        for (Card pathCard : pathCards) {
            if (pathCard.getPathId() == pathIdBehind) {
                if (pathCard.getCardType() != CardType.WATER && pathCard.isOnTop()) {
                    valueOfCardBehind = pathCard.getValue();
                }
            }
        }
        System.out.println("PRICE BEHIND - > " + valueOfCardBehind);
        return valueOfCardBehind;
    }

    /**
     * Hermann Grieder
     * <br>
     * Writes the game state HashMap after every turn which is then send to the server.
     *
     * @return HashMap with the current game state information
     */
    public HashMap<String, Object> writeGameStateMap() {
        HashMap<String, Object> gameStateMap = new HashMap<>();
        System.out.println("Score before we send the message - > " + players.get(currentTurn).getScore());
        gameStateMap.put("CurrentTurn", currentTurn);
        gameStateMap.put("PlayerId", players.get(currentTurn).getPlayerID());
        gameStateMap.put("GameName", players.get(currentTurn).getGameName());
        gameStateMap.put("GamePieceIndex", players.get(localPlayerId).getGamePieces().indexOf(selectedGamePiece));
        gameStateMap.put("TargetPathIds", targetPathIds);
        ArrayList<Integer> newPaidCardsIndices = new ArrayList<>();
        logger.info("Paid cards indices size: " + paidCardIndices.size());
        for (Integer integer : paidCardIndices) {
            logger.info(String.valueOf(integer));
            newPaidCardsIndices.add(integer);
        }

        gameStateMap.put("PaidCards", newPaidCardsIndices);

        // Strange behaviour: When I try to send playedCardsIndices directly, a maximum of one number arrives at the
        // server. So I finally tried to create a new ArrayList and it works. I do not know why this problem exists.
        ArrayList<Integer> newPlayedCardsIndices = new ArrayList<>();
        logger.info("Played cards indices size: " + playedCardsIndices.size());
        for (Integer integer : playedCardsIndices) {
            logger.info(String.valueOf(integer));
            newPlayedCardsIndices.add(integer);
        }

        // sending the new ArrayList instead of playedCardsIndices directly
        gameStateMap.put("PlayedCardsIndices", newPlayedCardsIndices);

        return gameStateMap;
    }

    /**
     * Hermann Grieder
     * <br>
     * Reads the initial game state map from the server.
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
     * Reads the game state map from the server while the game is in progress.
     *
     * @param gameStateMap
     */
    @SuppressWarnings("unchecked")
    public boolean readGameStateMap(HashMap<String, Object> gameStateMap) {
        previousTurn = currentTurn;
        currentTurn = (int) gameStateMap.get("CurrentTurn");
        players = (ArrayList<Player>) gameStateMap.get("Players");
        int score = (int) gameStateMap.get("Score");
        System.out.println("Score we receive on client side - > " + score);
        players.get(previousTurn).setScore(score);
        System.out.println("Score we have in our player - > " + players.get(previousTurn).getScore());
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
     * Updates values and lists in the model after a move has been validated by the server.
     */

    public void updateValues() {
        players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex).setCurrentPathId(targetPathIdRemote);

        if (indexOfPathCardToRemove != -1) {
            Card pathCardToRemove = pathCards.get(indexOfPathCardToRemove);
            pathCardToRemove.setPathId(-1);
            players.get(previousTurn).getPathCardStack().add(pathCardToRemove);
        }
        removePaidCardsFromStack();

        if (indexOfPathCardToShow != -1) {
            pathCards.get(indexOfPathCardToShow).setIsOnTop(true);
        }
        selectedGamePiece = players.get(previousTurn).getGamePieces().get(gamePieceUsedIndex);
        selectedGamePiece.setCurrentPathId(targetPathIdRemote);
        updateMovementCards();
    }

    /**
     * Fabian Witschi
     * <br>
     * Removes the cards that where used to pay for the crossing of water from the stack.
     */
    private void removePaidCardsFromStack() {
        if (!(paidCardIndices == null || paidCardIndices.size() == 0)) {
            ArrayList<Card> stacksCardsToRemove = new ArrayList<>();
            for (Integer index : paidCardIndices) {
                stacksCardsToRemove.add(players.get(previousTurn).getPathCardStack().get(index));
            }

            for (Card card : stacksCardsToRemove) {
                players.get(previousTurn).getPathCardStack().remove(card);
            }
        }
    }

    /**
     * Hermann Grieder
     * <br>
     * Updates the movement card arrayList in the player by removing the played cards and adding the new card(s)
     * from the server.
     */
    private void updateMovementCards() {
        ArrayList<Card> movementCardsToRemove = new ArrayList<>();
        for (Integer index : playedCardsIndices) {
            System.out.println("Index to remove " + index);
            movementCardsToRemove.add(players.get(previousTurn).getMovementCards().get(index));
        }

        for (Card card : movementCardsToRemove) {
            players.get(previousTurn).getMovementCards().remove(card);
        }

        for (Card card : deckCardToAdd) {
            players.get(previousTurn).getMovementCards().add(card);
        }
        playedCardsIndices.clear();
    }

    /**
     * Hermann Grieder
     * Finds the name of the winner of the game.
     *
     * @return The name of the winner
     */
    public String calculateWinner() {
        String winner = null;
        int score = 0;
        for (Player player : players) {
            if (player.getScore() > score) {
                score = player.getScore();
                winner = player.getPlayerName();
            } else if (player.getScore() == score) {
                winner = null;
            }
        }
        return winner;
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

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getPreviousTurn() {
        return previousTurn;
    }

    public void setPreviousTurn(int previousTurn) {
        this.previousTurn = previousTurn;
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

    public SimpleIntegerProperty priceToCrossWaterAutomatically() {
        return priceToCrossWaterAutomatically;
    }

    public void addToPlayedCards() {
        int index = players.get(localPlayerId).getMovementCards().indexOf(selectedCard);
        playedCardsIndices.add(index);
    }

    public ArrayList<Integer> getTargetPathIds() {
        return targetPathIds;
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

    public void clearPaidCardsIndices() {
        paidCardIndices.clear();
    }

    public void setPaidCorrectPrice(boolean paidCorrectPrice) {
        this.paidCorrectPrice = paidCorrectPrice;
    }

    public ArrayList<Integer> getPaidCardIndices() {
        return paidCardIndices;
    }
    public void setCantMoveButtonHasBeenPressed(boolean cantMoveButtonHasBeenPressed) {
        this.cantMoveButtonHasBeenPressed = cantMoveButtonHasBeenPressed;
    }
}
