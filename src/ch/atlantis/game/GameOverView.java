package ch.atlantis.game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Hermann Grieder on 11.12.2016.
 */
public class GameOverView extends Pane {
    private final VBox root;

    //TOP Element
    private Label lblGameOver;

    //CENTER Elements
    private GridPane centerPane;
    private Label lblUserName;

    //BOTTOM Elements
    private VBox bottomPane;


    private Button btnBackToLobby;

    private Label lblScore;

    public GameOverView(double height, double width) {

        root = new VBox(30);
        root.setMinHeight(height);
        root.setMinWidth(width);
        root.getChildren().add(createTop());
        root.getChildren().add(createCenter());
        root.getChildren().add(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }


    private Node createTop() {

        lblGameOver = new Label("GAME OVER");
        lblGameOver.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));

        return lblGameOver;
    }

    private Node createCenter() {

        centerPane = new GridPane();

        centerPane.add(lblUserName = new Label("Username: "), 0, 0);
        centerPane.add(lblScore = new Label("Score: "), 1, 0);

        return centerPane;
    }

    private Node createBottom() {

        bottomPane = new VBox(50);
        btnBackToLobby = new Button("Back To Lobby");
        bottomPane.getChildren().addAll(btnBackToLobby);
        return bottomPane;
    }

    private void defineStyleClass() {

        root.setId("root");

        /* Common Style Class for the buttons in the Login View*/
        btnBackToLobby.getStyleClass().add("buttons");




        /* Common Style Class for the Labels in the Login View*/
        lblGameOver.getStyleClass().add("labels");
        lblUserName.getStyleClass().add("labels");


        //TOP Element IDs
        lblGameOver.setId("gameOver_lblGameOver");

        // CENTER Elements IDs
        centerPane.setId("centerPane");
        lblUserName.setId("gameOver_lblUserName");


        //BOTTOM Elements IDs
        bottomPane.setId("bottomPane");


        btnBackToLobby.setId("gameOver_btnBackToLobby");


    }
}
