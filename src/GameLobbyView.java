import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class GameLobbyView extends Parent {
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
    private Label lblWindowTitle;
    private final BorderPane root;
    private VBox vBoxTop;
    private MenuBar menuBar;
    private Menu menuHelp;
    private Menu menuOptions;
    private Menu menuFile;
    private MenuItem gameRules;
    private HBox bottomHBox;
    private VBox rightVBox;
    private VBox centerVBox;
    private VBox leftVBox;

    public GameLobbyView(AtlantisView view) {
        this.view = view;

        root = new BorderPane();

        root.setTop(createTop());
        root.setLeft(createLeft());
        root.setCenter(createCenter());
        root.setRight(createRight());
        root.setBottom(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }

    private Node createTop() {

        vBoxTop = new VBox();

        menuBar = new MenuBar();
        menuFile = new Menu("File");
        menuOptions = new Menu("Options");
        menuHelp = new Menu("Help");

        gameRules = new MenuItem("Game Rules");

        menuHelp.getItems().add(gameRules);

        menuBar.getMenus().addAll(menuFile, menuOptions, menuHelp);

        lblWindowTitle = new Label("Welcome to Atlantis");

        vBoxTop.getChildren().addAll(menuBar, lblWindowTitle);

        return vBoxTop;
    }

    private Node createBottom() {

        bottomHBox = new HBox(10);

        lblStatus = new Label("Status: Disconnected");
        lblInfo = new Label("Information");
        bottomHBox.getChildren().addAll(lblStatus, lblInfo);

        return bottomHBox;
    }

    private Node createRight() {

        rightVBox = new VBox();

        txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setWrapText(true);
        txtField = new TextField();
        rightVBox.getChildren().addAll(txtArea, txtField);

        return rightVBox;
    }

    private Node createCenter() {

        centerVBox = new VBox();

        Label lblGameTitles = new Label("Games");
        gameList = new ListView();
        gameList.getItems().add(0, "Hallo");
        centerVBox.getChildren().addAll(lblGameTitles, gameList);
        return centerVBox;
    }

    private Node createLeft() {

        leftVBox = new VBox();

        btnCreateGame = new Button("Create Game");
        btnLogin = new Button("Login");
        btnCreateProfile = new Button("Create Profile");
        btnOptions = new Button("Options");

        leftVBox.getChildren().addAll(btnCreateGame, btnLogin, btnCreateProfile, btnOptions);
        return leftVBox;
    }

    private void defineStyleClass() {

        /*
        CSS Classes for the buttons in the LEFT part of the Border Pane
         */

        btnCreateGame.getStyleClass().add("leftButtons");
        btnLogin.getStyleClass().add("leftButtons");
        btnCreateProfile.getStyleClass().add("leftButtons");
        btnOptions.getStyleClass().add("leftButtons");

        /*
            CSS IDs for the ROOT border pane of the game lobby.
            Contains all the other elements of the game lobby.
        */
        root.setId("root");

        //  CSS IDs for the TOP part of the game lobby (menuBar and Title)
        vBoxTop.setId("vBoxTop");
        menuBar.setId("menuBar");
        menuFile.setId("menuFile");
        menuOptions.setId("menuOptions");
        menuHelp.setId("menuHelp");
        lblWindowTitle.setId("lblWindowTitle");

        //  CSS IDs for the BOTTOM part of the game lobby (Status bar)
        bottomHBox.setId("bottomHBox");
        lblStatus.setId("lblStatus");
        lblInfo.setId("lblInfo");

        //  CSS IDs for the RIGHT part of the game lobby (Chat text area and the input text field)
        rightVBox.setId("rightVBox");
        txtArea.setId("txtArea");
        txtField.setId("txtField");

        //  CSS IDs for the CENTER part of the game lobby (small title and Game list)
        centerVBox.setId("centerVBox");
        gameList.setId("gameListView");

        //  CSS IDs for the LEFT part of the game lobby (Game Lobby buttons)
        leftVBox.setId("leftVBox");
        btnCreateGame.setId("btnCreateGame");
        btnLogin.setId("btnLogin");
        btnCreateProfile.setId("btnCreateProfile");
        btnOptions.setId("btnOptions");
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

    public Label getlblStatus() {
        return lblStatus;
    }

    public MenuItem getGameRules() {
        return this.gameRules;
    }

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }

}
