package ch.atlantis.game;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

/**
 * Created by Hermann Grieder on 03.09.2016.
 * A gamePiece extends a JavaFx circle and holds a currentPathId, x and y coordinates.
 */
public class GamePiece extends Circle implements Serializable {

    private static final long serialVersionUID = 7661939850705259125L;
    private int startPathId;
    private int currentPathId;

    public void move(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public int getStartPathId() {
        return startPathId;
    }

    public void setStartPathId(int startPathId) {
        this.startPathId = startPathId;
    }

    public void setCurrentPathId(int currentPathId) {
        this.currentPathId = currentPathId;
    }

    public int getCurrentPathId() {
        return currentPathId;
    }

    public void resetPathId() {
        currentPathId = startPathId;
    }
}
