package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
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
        } else {
            view.createIntroView();
            new IntroController(model, view);
        }
    }
}