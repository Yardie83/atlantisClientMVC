import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Loris Grether on 04.08.2016.
 */
public class LoginView extends Parent {

    private AtlantisView view;

    private final VBox root;

    //TOP Element
    private Label lblLogin;

    //CENTER Elements
    private GridPane centerPane;
    private Label lblUserName;
    private TextField txtUserName;
    private Label lblPassword;
    private TextField txtPassword;

    //BOTTOM Elements
    private VBox bottomPane;

    private HBox buttonRow1;
    private Button btnLogin;
    private Button btnCancel;

    private HBox buttonRow2;
    private Button btnCreateProfile;
    private Button btnPlayAsGuest;

    public LoginView(AtlantisView view) {
        this.view = view;

        root = new VBox(30);
        root.setMinHeight(view.getPrimaryStage().getHeight());
        root.setMinWidth(view.getPrimaryStage().getWidth());
        root.getChildren().add(createTop());
        root.getChildren().add(createCenter());
        root.getChildren().add(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }


    private Node createTop() {

        lblLogin = new Label("LOGIN");

        return lblLogin;
    }

    private Node createCenter() {

        centerPane = new GridPane();

        centerPane.add(lblUserName = new Label("Username: "), 0, 0);
        centerPane.add(txtUserName = new TextField(), 1, 0);

        centerPane.add(lblPassword = new Label("Password: "), 0, 1);
        centerPane.add(txtPassword = new PasswordField(), 1, 1);

        return centerPane;
    }

    private Node createBottom() {

        bottomPane = new VBox(50);

        buttonRow1 = new HBox(20);
        buttonRow2 = new HBox(20);

        buttonRow1.getChildren().add(btnLogin = new Button("Login"));
        buttonRow1.getChildren().add(btnCancel = new Button("Cancel"));
        buttonRow2.getChildren().add(btnCreateProfile = new Button("Create Profile"));
        //TODO: Do we really need the Play as Guest button?
        buttonRow2.getChildren().add(btnPlayAsGuest = new Button("Play as Guest"));

        bottomPane.getChildren().addAll(buttonRow1,buttonRow2);

        return bottomPane;
    }

    private void defineStyleClass() {

        root.setId("root");

        /* Common Style Class for the buttons in the Login View*/
        btnCancel.getStyleClass().add("buttons");
        btnLogin.getStyleClass().add("buttons");
        btnCreateProfile.getStyleClass().add("buttons");
        btnPlayAsGuest.getStyleClass().add("buttons");

        /* Common Style Class for the TextFields in the Login View*/
        txtUserName.getStyleClass().add("textFields");
        txtPassword.getStyleClass().add("textFields");

        /* Common Style Class for the Labels in the Login View*/
        lblLogin.getStyleClass().add("labels");
        lblUserName.getStyleClass().add("labels");
        lblPassword.getStyleClass().add("labels");

        //TOP Element IDs
        lblLogin.setId("lblLogin");

        // CENTER Elements IDs
        centerPane.setId("centerPane");
        lblUserName.setId("lblUserName");
        txtUserName.setId("txtUserName");
        lblPassword.setId("lblPassword");
        txtPassword.setId("txtPassword");

        //BOTTOM Elements IDs
        bottomPane.setId("bottomPane");
        buttonRow1.setId("buttonRow1");
        btnLogin.setId("btnLogin");
        btnCancel.setId("btnCancel");
        buttonRow2.setId("buttonRow2");
        btnCreateProfile.setId("btnCreateProfile");
        btnPlayAsGuest.setId("btnPlayAsGuest");
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

    public Button getBtnPlayAsGuest() {
        return btnPlayAsGuest;
    }

    public TextField getTxtUserName() {
        return txtUserName;
    }

    public TextField getTxtPassword() {
        return txtPassword;
    }
}