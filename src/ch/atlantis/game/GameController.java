package ch.atlantis.game;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;

/**
 * Created by Hermann Grieder on 31.08.2016.
 */
public class GameController {


    private AtlantisView view;

    AtlantisModel model;

    public GameController(AtlantisModel model, AtlantisView view) {

        this.model = model;
        this.view = view;
    }
}
