import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Hermann Grieder on 08.08.2016.
 */
public class CreateGameView extends Parent {

    private TextField txtGameName;

    private Button btnCancel;
    private Button btnCreateNewGame;

    private Label lblNewGame;
    private Label lblGameName;
    private Label lblNoOfPlayers;

    private GridPane centerPane;

    private HBox bottomPane;
    private final VBox root;
    private RadioButton radioButtonTwoPlayers;
    private RadioButton radioButtonFourPlayers;
    private ToggleGroup toggleGroup;

    public CreateGameView(AtlantisView view) {

        root = new VBox(30);
        root.setMinHeight(view.getPrimaryStage().getHeight());
        root.setMinWidth(view.getPrimaryStage().getWidth());
        root.getChildren().add(createTop());
        root.getChildren().add(createCenter());
        root.getChildren().add(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }

    private Label createTop() {

        lblNewGame = new Label("NEW GAME");
        lblNewGame.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));

        return lblNewGame;
    }

    private Node createCenter() {

        centerPane = new GridPane();

        centerPane.add(lblGameName = new Label("Game name: "), 0, 0);
        centerPane.add(txtGameName = new TextField(), 1, 0);

        centerPane.add(lblNoOfPlayers = new Label("Number of Players: "), 0, 1);

        HBox noOfPlayerBox = new HBox(20);

        toggleGroup = new ToggleGroup();
        radioButtonTwoPlayers = new RadioButton("2");
        radioButtonTwoPlayers.setToggleGroup(toggleGroup);
        radioButtonFourPlayers = new RadioButton("4");
        radioButtonFourPlayers.setToggleGroup(toggleGroup);

        noOfPlayerBox.getChildren().addAll(radioButtonTwoPlayers, radioButtonFourPlayers);

        centerPane.add(noOfPlayerBox, 1, 1);

        return centerPane;
    }

    private Node createBottom() {

        bottomPane = new HBox();

        bottomPane.getChildren().add(btnCreateNewGame = new Button("Create"));
        bottomPane.getChildren().add(btnCancel = new Button("Cancel"));

        return bottomPane;
    }

    private void defineStyleClass() {

        /*Common Style Class for the Labels and TextFields in the CENTER*/
        lblGameName.getStyleClass().add("labelsCenter");
        lblNoOfPlayers.getStyleClass().add("labelsCenter");

        /* Common Style Class for the buttons in the Create Game View*/
        btnCancel.getStyleClass().add("buttonsBottom");
        btnCreateNewGame.getStyleClass().add("buttonsBottom");

        /* Common Style Class for the textField in the Create Game View*/
        txtGameName.getStyleClass().add("textFieldsCenter");

        /* Common Style Class for the RadioButtons in the Create Game View*/
        radioButtonTwoPlayers.getStyleClass().add("radioButtons");
        radioButtonFourPlayers.getStyleClass().add("radioButtons");

        /*Style ID for the root BorderPane */
        root.setId("root");

        /*Style IDs for the controls in the Create Game View*/

        //TOP element
        txtGameName.setId("txtGameName");

        //CENTER elements
        centerPane.setId("centerPane");
        lblNewGame.setId("lblNewGame");
        lblGameName.setId("lblGameName");
        lblNoOfPlayers.setId("lblNoOfPlayers");

        //BOTTOM elements
        bottomPane.setId("bottomPane");
        btnCancel.setId("btnCancel");
        btnCreateNewGame.setId("btnCreateNewGame");
    }

    public TextField getTxtGameName() {
        return txtGameName;
    }

    public ToggleGroup getTgNoOfPlayers() {
        return toggleGroup;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnCreateNewGame() {
        return btnCreateNewGame;
    }

}