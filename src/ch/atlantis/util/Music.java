package ch.atlantis.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;

/**
 * Created by LorisGrether on 11.10.2016.
 */
public class Music extends Thread {

    private MediaPlayer myPlayer;

    @Override
    public void run() {

        Media backgroundMusic = new Media(Paths.get("src/ch/atlantis/res/Maid with the Flaxen Hair.mp3").toUri()
                .toString());

        myPlayer = new MediaPlayer(backgroundMusic);
        myPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        myPlayer.play();

    }

    public void stopMusic(){

        myPlayer.stop();


    }


}