package ch.atlantis.game;

import ch.atlantis.util.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hermann Grieder on 28.10.2016
 */
public class GameModel {

    HashMap<String, ArrayList> initList;

    public GameModel(Message message){
        initList = (HashMap<String, ArrayList>) message.getMessageObject();
    }


}
