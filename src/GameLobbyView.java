import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 *
 */
public class GameLobbyView extends Parent implements ChangeListener {
    private AtlantisView view;
    private Label lblStatus;
    private Label lblInfo;
    private TextArea txtArea;
    private TextField txtField;
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

        VBox vBox = new VBox();

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuOptions = new Menu("Options");
        Menu menuRules = new Menu("Game Rules");
        
        menuBar.getMenus().addAll(menuFile, menuOptions, menuRules);
                
        Label lblWindowTitle = new Label("Welcome to Atlantis");

        vBox.getChildren().addAll(menuBar, lblWindowTitle);

        return vBox;
    }

    private Node createBottom() {

        HBox hBox = new HBox(10);

        lblStatus = new Label("Status: Disconnected");
        lblInfo = new Label("Information");
        hBox.getChildren().addAll(lblStatus, lblInfo);

        return hBox;
    }

    private Node createRight() {

        VBox vBox = new VBox();

        txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setWrapText(true);
        txtField = new TextField();
        vBox.getChildren().addAll(txtArea, txtField);

        return vBox;
    }

    private Node createCenter() {

        VBox vBox = new VBox();

        Label lblGameTitles = new Label("Games");
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

    public Label getLblInfo() {
        return lblInfo;
    }

    public void setLblInfo(Label lblInfo) {
        this.lblInfo = lblInfo;
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

    public Label getlblStatus(){
        return lblStatus;
    }

    public void setLblStatus(Label lblStatus){
        this.lblStatus = lblStatus;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        txtArea.appendText(newValue.toString());
    }
}
