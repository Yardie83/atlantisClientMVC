package ch.atlantis.controller;

import ch.atlantis.model.AtlantisModel;
import ch.atlantis.view.AtlantisView;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.Collections;
import java.util.Random;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 */

public class AtlantisController {

    //Set debugMode to "true" in order to skip the intro video
    public final static boolean debugMode = true;

    public AtlantisController(AtlantisModel model, AtlantisView view) {

        if (debugMode) {
            view.createGameLobbyView();
            System.out.println("DebugMode is on.\nIntro was skipped");
            new GameLobbyController(model, view);
            view.getGameLobbyView().show();
        } else {
            view.createIntroView();
            new IntroController(model, view);
        }
    }
}