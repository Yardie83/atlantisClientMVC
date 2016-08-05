import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Loris Grether on 04.08.2016.
 */
public class LoginView extends Parent{

    private AtlantisView view;

    private TextField txtUserName;
    private TextField txtPassword;

    private Button btnCancel;
    private Button btnLogin;
    private Button btnCreateProfile;
    private Button btnPlayAsGuest;

    public LoginView(AtlantisView view){
        this.view = view;

        BorderPane root = new BorderPane();
        root.setTop(new Label("Login:"));
        root.setCenter(createCenter());
        root.setBottom(createBottom());

        this.getChildren().add(root);
    }

    private Node createCenter(){

        GridPane pane = new GridPane();

        pane.add(new Label("Username: "), 0, 0);
        pane.add(txtUserName = new TextField(), 1, 0);

        pane.add(new Label("Password: "), 0, 1);
        pane.add(txtPassword = new PasswordField(), 1, 1);

        return pane;
    }

    private Node createBottom(){

        GridPane pane = new GridPane();

        pane.add(btnLogin = new Button("Login"), 0, 0);
        pane.add(btnCreateProfile = new Button("Create Profile"), 0, 1);
        pane.add(btnPlayAsGuest = new Button("Play as Guest"), 1, 1);
        pane.add(btnCancel = new Button("Cancel"), 0, 3);

        return pane;
    }

    public Button getBtnCancel() {return btnCancel;}
    public Button getBtnLogin() {return btnLogin;}
    public Button getBtnCreateProfile() {return btnCreateProfile;}
    public Button getBtnPlayAsGuest() {return btnPlayAsGuest;}

    public TextField getTxtUserName() {return txtUserName;}
    public TextField getTxtPassword() {return txtPassword;}
}