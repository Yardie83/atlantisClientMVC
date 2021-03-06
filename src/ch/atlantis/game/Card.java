package ch.atlantis.game;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.Hashtable;


/**
 * Hermann Grieder
 * <br>
 *  The different cards types in the game.
 */
enum CardType {
    PATH, WATER, START, END, MOVEMENT
}

/**
 * Created by Hermann Grieder on 15/08/16.
 * <p>
 * Card class that defines the color and the image for the individual card
 */
public class Card extends Rectangle implements Serializable {

    private static final long serialVersionUID = 1597939850705259874L;

    private boolean isOnTop;
    private int value;
    private int colorSet;
    private CardType cardType;
    private int pathID;


    /**
     * Loris Grether
     * In order to be able to add the cards like this I had to rename the cards
     * Adds the scanned images to the cards.
     */
    public void applyCardImages(Hashtable<String, ImageView> listCardImages) {

        if (this.getCardType() == CardType.WATER){
            //rectangle setFill
            super.setFill(new ImagePattern(listCardImages.get("water.jpg").getImage()));

        }else if (this.getCardType() == CardType.PATH) {

            super.setFill(new ImagePattern(listCardImages.get(colorSet + "_" + value + ".jpg").getImage()));

        } else if (getCardType() == CardType.MOVEMENT) {

            super.setFill(new ImagePattern(listCardImages.get("card_" + colorSet + ".jpg").getImage()));

        } else if (getCardType() == CardType.START) {

            super.setFill(new ImagePattern(listCardImages.get("atlantis.png").getImage()));

        } else if (getCardType() == CardType.END) {

            super.setFill(new ImagePattern(listCardImages.get("land.png").getImage()));
        }
    }

    public int getValue() {
        return value;
    }

    public int getColorSet() {
        return colorSet;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setPathId(int pathID) {
        this.pathID = pathID;
    }

    public int getPathId() {
        return this.pathID;
    }

    public void setIsOnTop(boolean isOnTop) {
        this.isOnTop = isOnTop;
    }

    public boolean isOnTop() {
        return this.isOnTop;
    }

}
