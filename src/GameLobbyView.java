import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.image.*;

import java.awt.*;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class GameLobbyView extends Pane {

    private Stage gameLobbyStage;
    private Scene gameLobbyScene;

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
    private MenuItem menuItemExit;
    private MenuItem menuItemGameRules;
    private HBox bottomHBox;
    private VBox rightVBox;
    private VBox centerVBox;
    private VBox leftVBox;

    public GameLobbyView() {

        String css = this.getClass().getResource("/res/css_GameLobby.css").toExternalForm();
        gameLobbyScene = new Scene(this);
        gameLobbyScene.getStylesheets().add(css);
        gameLobbyStage = new Stage();
        gameLobbyStage.setHeight(AtlantisView.HEIGHT);
        gameLobbyStage.setWidth(AtlantisView.WIDTH);
        gameLobbyStage.setScene(gameLobbyScene);

        Image image = new Image("/res/Fishi.png");
        gameLobbyScene.setCursor(new ImageCursor(image));

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

        menuItemExit = new MenuItem("Exit");
        menuItemGameRules = new MenuItem("Game Rules");

        menuFile.getItems().add(menuItemExit);
        menuHelp.getItems().add(menuItemGameRules);

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
        menuItemExit.setId("menuItemExit");
        menuItemGameRules.setId("menuItemGameRules");
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

    public void show(){
        this.gameLobbyStage.show();
    }

    public Stage getGameLobbyStage() {
        return gameLobbyStage;
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

    public Label getLblStatus() {
        return lblStatus;
    }

    public Menu getMenuOptions() {
        return menuOptions;
    }

    public MenuItem getMenuItemExit() {
        return menuItemExit;
    }

    public MenuItem getMenuItemGameRules() {
        return this.menuItemGameRules;
    }

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }
}
