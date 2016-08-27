package ch.atlantis.view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class GameLobbyView extends Pane {

    private Stage gameLobbyStage;

    private Label lblStatus;
    private Label lblInfo;
    private TextArea txtArea;
    private TextField txtField;
    private ListView<String> gameListView;
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
    private Pane popup;
    private Separator s0;
    private Label lblGameTitles;
    private ArrayList<Node> gameLobbyControls = new ArrayList<>();
    private Button btnStartGame;

    public GameLobbyView(int height, int width) {

        String css = this.getClass().getResource("/res/css_GameLobbyView.css").toExternalForm();
        Scene gameLobbyScene = new Scene(this);
        gameLobbyScene.getStylesheets().add(css);
        gameLobbyStage = new Stage();
        gameLobbyStage.setHeight(height);
        gameLobbyStage.setWidth(width);
        gameLobbyStage.setScene(gameLobbyScene);

//        //Set Mouse Cursor Image
//        Image image = new Image("/res/Fishi.png");
//        gameLobbyScene.setCursor(new ImageCursor(image));

        root = new BorderPane();

        root.setTop(createTop());
        root.setLeft(createLeft());
        root.setCenter(createCenter());
        root.setRight(createRight());
        root.setBottom(createBottom());

        root.minHeightProperty().bind(gameLobbyStage.heightProperty().subtract(40));
        root.minWidthProperty().bind(gameLobbyStage.widthProperty().subtract(10));

        defineStyleClass();

        this.getChildren().addAll(root);
        this.getControls(root);
    }

    private void getControls(Pane pane) {

        for (Node node : pane.getChildren()) {
            if (node instanceof Pane) {
                getControls((Pane) node);
            } else if (node instanceof Control)
                gameLobbyControls.add(node);
        }
    }

    private Node createTop() {

        vBoxTop = new VBox();

        menuBar = new MenuBar();

        menuFile = new Menu("File");
        menuOptions = new Menu("Options");
        menuHelp = new Menu("Help");

        menuItemExit = new MenuItem("Exit");
        menuItemGameRules = new MenuItem("ch.atlantis.game.Game Rules");

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
        Separator s = new Separator();
        s.setOrientation(Orientation.VERTICAL);
        lblInfo = new Label("");
        bottomHBox.getChildren().addAll(lblStatus, s, lblInfo);

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

        centerVBox = new VBox(30);
        lblGameTitles = new Label("Games");
        lblGameTitles.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));
        gameListView = new ListView();
        gameListView.getItems().addAll("Hallo", "ch.atlantis.game.Game #2 Hallo", "My ch.atlantis.game.Game 12.1.16", "ch.atlantis.game.Game #6 Doodle", "Hallo", "ch.atlantis.game.Game #5 Lol", "Hallo", "ch.atlantis.game.Game #2");
        centerVBox.getChildren().addAll(lblGameTitles, gameListView);
        gameListView = new ListView();
        centerVBox.getChildren().addAll(lblGameTitles, gameListView);
        return centerVBox;
    }

    private Node createLeft() {

        leftVBox = new VBox(10);
        // leftVBox.setTranslateX(-150);
        btnCreateGame = new Button("Create ch.atlantis.game.Game");
        btnLogin = new Button("Login");
        btnCreateProfile = new Button("Create Profile");
        btnOptions = new Button("Options");
        s0 = new Separator();
        Separator s1 = new Separator();
        Separator s2 = new Separator();
        Separator s3 = new Separator();
        btnStartGame = new Button("Start ch.atlantis.game.Game");


        leftVBox.getChildren().addAll(btnCreateGame, s0, btnLogin, s1, btnCreateProfile, s2, btnOptions, s3, btnStartGame);
        return leftVBox;
    }

    public void createPopUp(String message, int inset) {
        popup = new Pane();
        popup.setTranslateX(gameLobbyStage.getWidth());
        popup.setTranslateY(gameLobbyStage.getHeight() - 110);
        Label lblPopup = new Label(message);

        // CSS ID for the PopUp
        popup.setId("panePopup");
        lblPopup.setId("lblPopup");

        popup.getChildren().add(lblPopup);
        this.getChildren().add(popup);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1200), new KeyValue(popup.translateXProperty(), gameLobbyStage.getWidth() - 200, Interpolator.TANGENT(Duration.millis(3000), 500))));
        timeline.setAutoReverse(true);
        timeline.setCycleCount(2);
        timeline.setDelay(Duration.millis(200));
        timeline.play();
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.setVisible(false);
            }
        });
    }

    private void defineStyleClass() {

        /*
         *CSS Classes for the buttons in the LEFT part of the Border Pane
         */

        btnCreateGame.getStyleClass().add("leftButtons");
        btnLogin.getStyleClass().add("leftButtons");
        btnCreateProfile.getStyleClass().add("leftButtons");
        btnOptions.getStyleClass().add("leftButtons");

        /*
         *CSS ID for the ROOT border pane of the game lobby.
         *Contains all the other elements of the game lobby.
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

        //  CSS IDs for the CENTER part of the game lobby (small title and ch.atlantis.game.Game list)
        centerVBox.setId("centerVBox");
        lblGameTitles.setId("lblGameTitles");
        gameListView.setId("gameListView");

        //  CSS IDs for the LEFT part of the game lobby (ch.atlantis.game.Game Lobby buttons)
        leftVBox.setId("leftVBox");
        btnCreateGame.setId("btnCreateGame");
        btnLogin.setId("btnLogin");
        btnCreateProfile.setId("btnCreateProfile");
        btnOptions.setId("btnOptions");

    }

    public void show() {
        this.gameLobbyStage.show();
    }

    public Stage getGameLobbyStage() {
        return gameLobbyStage;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public Label getLblInfo() {
        return lblInfo;
    }

    public TextArea getTxtArea() {
        return txtArea;
    }

    public TextField getTxtField() {
        return txtField;
    }

    public ListView getGameListView() {
        return gameListView;
    }

    public Button getBtnCreateGame() {
        return btnCreateGame;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public Button getBtnCreateProfile() {
        return btnCreateProfile;
    }

    public Button getBtnOptions() {
        return btnOptions;
    }

    public Button getBtnStartGame(){
        return btnStartGame;
    }

    public Label getLblStatus() {
        return lblStatus;
    }

    public Label getLblWindowTitle() {
        return lblWindowTitle;
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

    public void removeLoginBtn() {
        leftVBox.getChildren().removeAll(btnLogin, s0);
    }

    public ArrayList<Node> getGameLobbyControls() {
        return gameLobbyControls;
    }
}
