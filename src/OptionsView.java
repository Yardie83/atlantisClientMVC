import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Hermann Grieder on 21.07.2016.
 *
 */
public class OptionsView extends Parent {

    //TODO Try to add this view with Modality.WINDOW
    public OptionsView() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);

        stage.show();
    }
}
