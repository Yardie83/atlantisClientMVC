package ch.atlantis.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Fabian on 15/08/16.
 */

enum CardType{
    //EMPTY = 0, PATH = 1, WATER = 2, START = 3, END = 4, HANDCARD = 5, BRIDGE = 6, HAND = 7, DECK = 8; MOVEMENT = 9;
    EMPTY, PATH, WATER, START, END, CARD, BRIDGE, HAND, DECK, MOVEMENT
}

public class Card extends Rectangle{

    int value;
    CardType cardType;


    // Constructor for Start, End, Bridge and Water Cards
    public Card(CardType cardType) {
        this.cardType = cardType;
    }

    // Constructor for Movement Cards. They do not have a value associated.
    public Card(int colorSet, CardType cardType){

        this.cardType = cardType;
        applyColorSet(colorSet);
    }

    // Constructor for Path Cards. They do have a value associated.
    public Card(int colorSet, int value, CardType cardType) {

        this.value = value;
        this.cardType = cardType;
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
        }
    }

    public int getValue() {
        return value;
    }

    public CardType getCardType() {
        return cardType;
    }

}
