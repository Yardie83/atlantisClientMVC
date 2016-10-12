package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 * Main Controller class. Depending on the value of the debugMode
 * calls for the creation of the IntroView and IntroController
 * (debugMode = false) or skips it and calls for the creation
 * of the GameLobbyView and the GameLobbyController (debugMode = true).
 *
 *
 */

public class AtlantisController {

    //Set debugMode to "true" in order to skip the intro video
    public final static boolean debugMode = true;

    public AtlantisController(AtlantisModel model, AtlantisView view) {

        if (debugMode) {
            view.createGameLobbyView(false);
            System.out.println("DebugMode is on.\nIntro was skipped");
            new GameLobbyController(model, view);
            view.getGameLobbyView().show();
            model.connectToServer();
        } else {
            view.createIntroView();
            new IntroController(model, view);
            view.getIntroStage().show();
        }
    }
}
