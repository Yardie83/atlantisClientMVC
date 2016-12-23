package ch.atlantis.game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Hermann Grieder on 11.12.2016.
 * The Game Over View for when the game is finished. It shows the score of all the players
 * and the winner. From this view the players can go back to the gameLobby.
 */
public class GameOverView extends Pane {
    private final VBox root;
    private final GameModel gameModel;

    //TOP Element
    private Label lblGameOver;

    //CENTER Elements
    private GridPane centerPane;

    //BOTTOM Elements
    private VBox bottomPane;


    private Button btnBackToLobby;


    public GameOverView(double height, double width, GameModel gameModel) {

        this.gameModel = gameModel;

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

        String winnerName = gameModel.calculateWinner();

        for (int i = 0; i < gameModel.getPlayers().size(); i++) {
            centerPane.add(new Label("Player: " + gameModel.getPlayers().get(i).getPlayerName()), 0, i);
            centerPane.add(new Label("Score: " + gameModel.getPlayers().get(i).getScore()), 1, i);
            if (gameModel.getPlayers().get(i).getPlayerName().equals(winnerName)) {
                centerPane.add(new Label("Winner"), 2, i);
            }
            if(winnerName == null){
                centerPane.add(new Label("Draw"), 2, i);
            }
        }
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

        //TOP Element IDs
        lblGameOver.setId("gameOver_lblGameOver");

        // CENTER Elements IDs
        centerPane.setId("centerPane");

        //BOTTOM Elements IDs
        bottomPane.setId("bottomPane");


        btnBackToLobby.setId("gameOver_btnBackToLobby");
    }

    public Button getBtnBackToLobby() {
        return btnBackToLobby;
    }
}
