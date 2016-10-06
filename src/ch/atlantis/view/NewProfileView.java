package ch.atlantis.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by LorisGrether on 04.08.2016.
 */
public class NewProfileView extends Pane {

    private TextField txtUserName;
    private TextField txtPassword;
    private TextField txtPasswordRevision;

    private Button btnCancel;
    private Button btnCreateProfile;

    private Label lblNewProfile;
    private Label lblUsername;
    private Label lblPassword;
    private Label lblPasswordRevision;
    private Label lblError;
    private GridPane centerPane;
    private HBox bottomPane;
    private final VBox root;

    public NewProfileView(int height, int width) {

        root = new VBox(30);
        root.setMinHeight(height);
        root.setMinWidth(width);
        root.getChildren().add(createTop());
        root.getChildren().add(createCenter());
        root.getChildren().add(createBottom());

        defineStyleClass();

        this.getChildren().add(root);
    }

    private Label createTop() {

        lblNewProfile = new Label("NEW PROFILE");
        lblNewProfile.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.LIGHTGREY, 2, 0.2, 0, 2));

        return lblNewProfile;
    }

    private Node createCenter() {

        centerPane = new GridPane();

        centerPane.add(lblUsername = new Label("Username: "), 0, 0);
        centerPane.add(txtUserName = new TextField(), 1, 0);

        centerPane.add(lblPassword = new Label("Password: "), 0, 1);
        centerPane.add(txtPassword = new TextField(), 1, 1);

        centerPane.add(lblPasswordRevision = new Label("Re-Type Password: "), 0, 2);
        centerPane.add(txtPasswordRevision = new TextField(), 1, 2);

        centerPane.add(lblError = new Label(""), 0,3,2,1);

        return centerPane;
    }

    private Node createBottom() {

        bottomPane = new HBox();

        bottomPane.getChildren().add(btnCreateProfile = new Button("Create"));
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
        lblError.getStyleClass().add("labelsCenter");
        lblError.setVisible(false);

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
        lblError.setId("lblError");

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

    public Label getLblError() {
        return lblError;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnCreateProfile() {
        return btnCreateProfile;
    }

}