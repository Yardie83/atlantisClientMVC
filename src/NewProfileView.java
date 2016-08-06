import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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

    private Label lblNewProfile;
    private Label lblUsername;
    private Label lblPassword;
    private Label lblPasswordRevision;
    private GridPane centerPane;
    private HBox bottomPane;
    private final BorderPane root;
    private HBox topPane;

    public NewProfileView(AtlantisView view) {
        this.view = view;

        root = new BorderPane();
        root.setTop(createTop());
        root.setCenter(createCenter());
        root.setBottom(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }

    private HBox createTop() {
        topPane = new HBox();

        lblNewProfile = new Label("New Profile:");

        topPane.getChildren().add(lblNewProfile);

        return topPane;
    }

    private Node createCenter() {

        centerPane = new GridPane();

        centerPane.add(lblUsername = new Label("Username: "), 0, 0);
        centerPane.add(txtUserName = new TextField(), 1, 0);

        centerPane.add(lblPassword = new Label("Password: "), 0, 1);
        centerPane.add(txtPassword = new TextField(), 1, 1);

        centerPane.add(lblPasswordRevision = new Label("Re-Type Password: "), 0, 2);
        centerPane.add(txtPasswordRevision = new TextField(), 1, 2);

        return centerPane;
    }

    private Node createBottom() {

        bottomPane = new HBox();

        bottomPane.getChildren().add(btnCreateProfile = new Button("Create Profile"));
        bottomPane.getChildren().add(btnCancel = new Button("Cancel"));

        return bottomPane;
    }

    private void defineStyleClass() {

        /* Common Style Class for the buttons in the New Profile View*/
        btnCancel.getStyleClass().add("buttonsBottom");
        btnCreateProfile.getStyleClass().add("buttonsBottom");

        /*Common Style Class for the Labels and TextFields in the CENTER*/
        lblUsername.getStyleClass().add("labelsCenter");
        lblPassword.getStyleClass().add("labelsCenter");
        lblPasswordRevision.getStyleClass().add("labelsCenter");

        txtUserName.getStyleClass().add("textFieldsCenter");
        txtPassword.getStyleClass().add("textFieldsCenter");
        txtPasswordRevision.getStyleClass().add("textFieldsCenter");

        /*Style ID for the root BorderPane */
        root.setId("root");


        /*Style IDs for the controls in the New Profile View*/
        txtUserName.setId("txtUserName");
        txtPassword.setId("txtPassword");
        txtPasswordRevision.setId("txtPasswordRevision");

        btnCancel.setId("btnCancel");
        btnCreateProfile.setId("btnCreateProfile");

        lblNewProfile.setId("lblNewProfile");
        lblUsername.setId("lblUsername");
        lblPassword.setId("lblPassword");
        lblPasswordRevision.setId("lblPasswordRevision");

        topPane.setId("topPane");
        centerPane.setId("centerPane");
        bottomPane.setId("bottomPane");
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