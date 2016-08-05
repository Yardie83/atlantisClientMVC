import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Created by LorisGrether on 04.08.2016.
 */
public class NewProfileView extends Parent {

    private AtlantisView view;

    private TextField txtUserName;
    private TextField txtPassword;
    private TextField txtPasswordRevision;

    private Button btnCancel;
    private Button btnCreateProfile;

    public NewProfileView(AtlantisView view){
        this.view = view;

        BorderPane root = new BorderPane();
        root.setTop(new Label("New Profile:"));
        root.setCenter(createCenter());
        root.setBottom(createBottom());

        this.getChildren().add(root);
    }

    private Node createCenter(){

        GridPane pane = new GridPane();

        pane.add(new Label("Username: "), 0, 0);
        pane.add(txtUserName = new TextField(), 1, 0);

        pane.add(new Label("Password: "), 0, 1);
        pane.add(txtPassword = new TextField(), 1, 1);

        pane.add(new Label("Re-Type Password: "), 0, 2);
        pane.add(txtPasswordRevision = new TextField(), 1, 2);

        return pane;
    }

    private Node createBottom(){

        GridPane pane = new GridPane();

        pane.add(btnCreateProfile = new Button("Create Profile"), 0, 0);
        pane.add(btnCancel = new Button("Cancel"), 1, 0);

        return pane;
    }

    public TextField getTxtUserName() {
        return txtUserName;
    }

    public TextField getTxtPassword() {
        return txtPassword;
    }

    public TextField getTxtPasswordRevision() {
        return txtPasswordRevision;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnCreateProfile() {
        return btnCreateProfile;
    }
}