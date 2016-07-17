import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 *
 */

public class AtlantisController{

    final private AtlantisModel model;
    final private AtlantisView view;

    public AtlantisController(AtlantisModel model, AtlantisView view) {
        this.model = model;
        this.view = view;
        this.view.createIntroView();

        view.getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.createGameLobbyView();
                handleGameLobby();
                model.connectToServer();
            }
        });

        view.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                view.stop();
                Platform.exit();
            }
        });
    }

    private void handleGameLobby() {

        view.getGameLobbyView().getBtnExit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                view.stop();
                Platform.exit();
            }
        });

        view.getGameLobbyView().getTxtField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER){
                    TextField txtField = view.getGameLobbyView().getTxtField();
                    model.sendMessage(txtField.getText());
                    txtField.clear();
                }
            }
        });




    }
}