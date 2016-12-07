package ch.atlantis.game;

import javafx.scene.shape.Rectangle;

import java.io.Serializable;

/**
 * Created by Hermann Grieder on 03.09.2016.
 * A gamePiece extends a JavaFx rectangle and holds a currentPathId, x and y coordinates.
 */
public class GamePiece extends Rectangle implements Serializable {

    private static final long serialVersionUID = 7661939850705259125L;
    private int currentPathId;

    public void move(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public void setCurrentPathId(int currentPathId) {
        this.currentPathId = currentPathId;
    }

    public int getCurrentPathId() {
        return currentPathId;
    }

}
