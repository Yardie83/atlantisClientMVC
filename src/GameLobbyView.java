import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class GameLobbyView extends Parent {
    private AtlantisView view;
    private Label lblWindowTitle;
    private Label lblStatus;
    private Label lblInfos;
    private TextArea txtArea;
    private TextField txtField;
    private Label lblGameTitles;
    private ListView gameList;
    private Button btnCreateGame;
    private Button btnLogin;
    private Button btnCreateProfile;
    private Button btnOptions;
    private Button btnExit;

    public GameLobbyView(AtlantisView view) {
        this.view = view;

        BorderPane root = new BorderPane();

        root.setTop(createTop());
        root.setLeft(createLeftSide());
        root.setCenter(createCenter());
        root.setRight(createRight());
        root.setBottom(createBottom());

        this.getChildren().add(root);
    }

    private Node createTop() {

        lblWindowTitle = new Label("Welcome to Atlantis");

        return lblWindowTitle;
    }

    private Node createBottom() {

        HBox hBox = new HBox(10);

        lblStatus = new Label("Status");
        lblInfos = new Label("Infos");
        hBox.getChildren().addAll(lblStatus, lblInfos);

        return hBox;
    }

    private Node createRight() {

        VBox vBox = new VBox();

        txtArea = new TextArea();
        txtArea.setEditable(false);
        txtField = new TextField();
        vBox.getChildren().addAll(txtArea, txtField);

        return vBox;
    }

    private Node createCenter() {

        VBox vBox = new VBox();

        lblGameTitles = new Label("Games");
        gameList = new ListView();
        gameList.getItems().add(0, "Hallo");
        vBox.getChildren().addAll(lblGameTitles, gameList);
        return vBox;
    }

    private Node createLeftSide() {

        VBox vBox = new VBox();

        btnCreateGame = new Button("Create Game");
        btnLogin = new Button("Login");
        btnCreateProfile = new Button("Create Profile");
        btnOptions = new Button("Options");
        btnExit = new Button("Exit");

        vBox.getChildren().addAll(btnCreateGame, btnLogin, btnCreateProfile, btnOptions, btnExit);
        return vBox;
    }

    public Label getLblStatus() {
        return lblStatus;
    }

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }

    public Label getLblInfos() {
        return lblInfos;
    }

    public void setLblInfos(Label lblInfos) {
        this.lblInfos = lblInfos;
    }

    public TextArea getTxtArea() {
        return txtArea;
    }

    public void setTxtArea(TextArea txtArea) {
        this.txtArea = txtArea;
    }

    public TextField getTxtField() {
        return txtField;
    }

    public ListView getGameList() {
        return gameList;
    }

    public void setGameList(ListView gameList) {
        this.gameList = gameList;
    }

    public Button getBtnCreateGame() {
        return btnCreateGame;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public void setBtnLogin(Button btnLogin) {
        this.btnLogin = btnLogin;
    }

    public Button getBtnCreateProfile() {
        return btnCreateProfile;
    }

    public Button getBtnOptions() {
        return btnOptions;
    }

    public Button getBtnExit() {
        return btnExit;
    }
}
