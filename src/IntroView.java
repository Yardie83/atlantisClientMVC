import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class IntroView extends Parent{


    public IntroView() {

        Button button = new Button("IntroView");
        this.getChildren().add(button);

    }



}
