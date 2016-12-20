package ch.atlantis.view;

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
 * Created by LorisGrether on 16.12.2016.
 */
public class InformationView extends Pane {

    private VBox root;

    private Label lblInformation;

    private Label lblGameTime;
    private Label lblCumulatedGameTime;

    private Label lblGameTimeSolution;
    private Label lblCumulatedGameTimeSolution;

    private Label lblNumberOfGames;
    private Label lblNumberOfGamesSolution;

    private Button btnClose;

    public InformationView(int height, int width) {

        root = new VBox(30);
        root.setMinHeight(height);
        root.setMinWidth(width);

        root.getChildren().add(createTop());
        root.getChildren().add(createCenter());
        root.getChildren().add(createBottom());

        this.defineStyleClass();

        this.getChildren().add(root);
    }

    private Node createTop() {

        lblInformation = new Label("Information");
        lblInformation.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));

        return lblInformation;
    }

    private Node createCenter() {

        GridPane centerPane = new GridPane();
        centerPane.setId("centerPane");

        lblGameTime = new Label("Game Time: ");
        lblGameTimeSolution = new Label("1");

        lblCumulatedGameTime = new Label("Cumulated Game Time: ");
        lblCumulatedGameTimeSolution = new Label("2");

        lblNumberOfGames = new Label("Number of played Games: ");
        lblNumberOfGamesSolution = new Label("3");

        centerPane.add(lblGameTime, 0, 0);
        centerPane.add(lblGameTimeSolution, 1, 0);

        centerPane.add(lblCumulatedGameTime, 0, 1);
        centerPane.add(lblCumulatedGameTimeSolution, 1, 1);

        centerPane.add(lblNumberOfGames, 0, 2);
        centerPane.add(lblNumberOfGamesSolution, 1, 2);

        return centerPane;
    }

    private Node createBottom() {

        btnClose = new Button("Close");
        return btnClose;
    }

    private void defineStyleClass() {

        root.setId("root");

        btnClose.setId("information_btnCancel");

        lblInformation.setId("lblInformation");
    }

    public Button getBtnClose() {
        return btnClose;
    }
}