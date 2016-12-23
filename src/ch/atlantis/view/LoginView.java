package ch.atlantis.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Hermann Grieder and Loris Grether on 04.08.2016.
 *
 * Login View
 */
public class LoginView extends Pane {

    private final VBox root;

    //TOP Element
    private Label lblLogin;

    //CENTER Elements
    private GridPane centerPane;
    private Label lblUserName;
    private TextField txtUserName;
    private Label lblPassword;
    private TextField txtPassword;
    private Label lblError;

    //BOTTOM Elements
    private VBox bottomPane;

    private HBox buttonRow;
    private Button btnLogin;
    private Button btnCancel;

    private Button btnCreateProfile;

    public LoginView(int height, int width) {


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

        lblLogin = new Label("LOGIN");
        lblLogin.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));

        return lblLogin;
    }

    private Node createCenter() {

        centerPane = new GridPane();

        centerPane.add(lblUserName = new Label("Username: "), 0, 0);
        centerPane.add(txtUserName = new TextField(), 1, 0);

        centerPane.add(lblPassword = new Label("Password: "), 0, 1);
        centerPane.add(txtPassword = new PasswordField(), 1, 1);
        centerPane.add(lblError = new Label(""), 0, 2, 2, 1);

        return centerPane;
    }

    private Node createBottom() {

        bottomPane = new VBox(50);

        buttonRow = new HBox(20);

        buttonRow.getChildren().add(btnLogin = new Button("Login"));
        buttonRow.getChildren().add(btnCreateProfile = new Button("Create Profile"));
        buttonRow.getChildren().add(btnCancel = new Button("Cancel"));

        bottomPane.getChildren().addAll(buttonRow);

        return bottomPane;
    }

    private void defineStyleClass() {

        root.setId("root");

        /* Common Style Class for the buttons in the Login View*/
        btnCancel.getStyleClass().add("buttons");
        btnLogin.getStyleClass().add("buttons");
        btnCreateProfile.getStyleClass().add("buttons");

        /* Common Style Class for the TextFields in the Login View*/
        txtUserName.getStyleClass().add("textFields");
        txtPassword.getStyleClass().add("textFields");

        /* Common Style Class for the Labels in the Login View*/
        lblLogin.getStyleClass().add("labels");
        lblUserName.getStyleClass().add("labels");
        lblPassword.getStyleClass().add("labels");
        lblError.getStyleClass().add("labels");
        lblError.setVisible(false);

        //TOP Element IDs
        lblLogin.setId("login_lblLogin");

        // CENTER Elements IDs
        centerPane.setId("centerPane");
        lblUserName.setId("login_lblUserName");
        txtUserName.setId("txtUserName");
        lblPassword.setId("login_lblPassword");
        txtPassword.setId("txtPassword");
        lblError.setId("login_lblError");

        //BOTTOM Elements IDs
        bottomPane.setId("bottomPane");
        buttonRow.setId("buttonRow");
        btnLogin.setId("login_btnLogin");
        btnCancel.setId("login_btnCancel");
        btnCreateProfile.setId("login_btnCreateProfile");
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public Button getBtnCreateProfile() {
        return btnCreateProfile;
    }

    public TextField getTxtUserName() {
        return txtUserName;
    }

    public TextField getTxtPassword() {
        return txtPassword;
    }

    public Label getLblError() {
        return lblError;
    }
}
