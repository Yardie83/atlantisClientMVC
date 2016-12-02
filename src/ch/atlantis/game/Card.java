package ch.atlantis.game;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by Hermann Grieder on 15/08/16.
 * <p>
 * Card class that defines sets the color and the image for the individual card
 */

enum CardType {
    PATH, WATER, START, END, BRIDGE, MOVEMENT
}

public class Card extends Rectangle implements Serializable {

    private static final long serialVersionUID = 1597939850705259874L;

    private boolean isOnTop;
    private boolean isPlayed;
    private int value;
    private int colorSet;
    private CardType cardType;
    private int pathID;

    public final static int BROWN = 0;
    public final static int PINK = 1;
    public final static int GREY = 2;
    public final static int YELLOW = 3;
    public final static int GREEN = 4;
    public final static int BLUE = 5;
    public final static int WHITE = 6;


    public void applyColor(Hashtable<String, ImageView> listCardImages) {

        if (this.getCardType() == CardType.PATH && value != 0){

            if (this.colorSet == BROWN){

            }


        }
        if (getCardType() == CardType.MOVEMENT){

            super.setFill(new ImagePattern(listCardImages.get("card_" + colorSet + ".jpg").getImage()));
        }


        if (value != 0) {
            switch (this.colorSet) {
                case BROWN:
                    super.setFill(Color.BROWN);
                    super.setFill(new ImagePattern(listCardImages.get("brown_" + value + ".jpg").getImage()));
                    break;
                case PINK:
                    super.setFill(Color.PINK);
                    super.setFill(new ImagePattern(listCardImages.get("pink_" + value + ".jpg").getImage()));
                    break;
                case GREY:
                    super.setFill(Color.GREY);
                    super.setFill(new ImagePattern(listCardImages.get("grey_" + value + ".jpg").getImage()));
                    break;
                case YELLOW:
                    super.setFill(Color.YELLOW);
                    super.setFill(new ImagePattern(listCardImages.get("yellow_" + value + ".jpg").getImage()));
                    break;
                case GREEN:
                    super.setFill(Color.GREEN);
                    super.setFill(new ImagePattern(listCardImages.get("green_" + value + ".jpg").getImage()));
                    break;
                case BLUE:
                    super.setFill(Color.BLUE);
                    super.setFill(new ImagePattern(listCardImages.get("blue_" + value + ".jpg").getImage()));
                    break;
                case WHITE:
                    super.setFill(Color.WHITE);
                    super.setFill(new ImagePattern(listCardImages.get("white_" + value + ".jpg").getImage()));
                    break;
                case 7:
                    super.setFill(null);
                    break;
                default:
                    super.setFill(null);
            }
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

    public boolean isPlayed() {
        return this.isPlayed;
    }

    public void setIsPlayed(Boolean isPlayed) {
        this.isPlayed = isPlayed;
    }
}
