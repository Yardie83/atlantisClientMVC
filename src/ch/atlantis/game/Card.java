package ch.atlantis.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Fabian on 15/08/16.
 */

enum CardType{
    //EMPTY = 0, PATH = 1, WATER = 2, START = 3, END = 4, HANDCARD = 5, BRIDGE = 6, DECK = 7; MOVEMENT = 8;
    EMPTY, PATH, WATER, START, END, HANDCARD, BRIDGE, DECK, MOVEMENT
}

public class Card extends Rectangle{

    private boolean isOnTop;
    private int value;
    private int colorSet;
    private CardType cardType;
    private int pathID;


    // Constructor for Movement Cards. They do not have a value associated.
    public Card(int colorSet, CardType cardType){

        this.cardType = cardType;
        applyColorSet(colorSet);
    }

    // Constructor for Path Cards. They do have a value associated.
    public Card(int colorSet, int value, CardType cardType) {

        this.value = value;
        this.colorSet = colorSet;
        this.cardType = cardType;
        this.setStroke(Color.BLACK);
        applyColorSet(colorSet);
    }

    private void applyColorSet(int colorSet) {
        switch (colorSet) {
            case 0:
                super.setFill(Color.BROWN);
                break;
            case 1:
                super.setFill(Color.PINK);
                break;
            case 2:
                super.setFill(Color.GREY);
                break;
            case 3:
                super.setFill(Color.YELLOW);
                break;
            case 4:
                super.setFill(Color.GREEN);
                break;
            case 5:
                super.setFill(Color.BLUE);
                break;
            case 6:
                super.setFill(Color.WHITE);
                break;
            case 7:
                super.setFill(null);
                break;
            default:
                super.setFill(null);
        }
    }

    public int getValue() {
        return value;
    }

    public int getColorSet() { return colorSet; }

    public CardType getCardType() {
        return cardType;
    }

    public void setPathId(int pathID){
        this.pathID = pathID;
    }

    public int getpathId() {
        return this.pathID;
    }

    public void setIsOnTop(boolean isOnTop){
        this.isOnTop = isOnTop;
    }

    public boolean isOnTop(){
        return this.isOnTop;
    }
}
