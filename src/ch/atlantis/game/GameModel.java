package ch.atlantis.game;

import ch.atlantis.util.Message;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Hermann Grieder on 28.10.2016
 */
public class GameModel {

    private ArrayList<Player> players;
    private ArrayList<Tile> tiles;
    private ArrayList<Card> pathCards;
    private ArrayList<Card> deck;
    private Player localPlayer;

    private Hashtable<String, ImageView> listCardImages;

    @SuppressWarnings("unchecked")
    public GameModel(Message message, Player localPlayer) {

        if (message.getMessageObject() instanceof HashMap) {
            HashMap<String, ArrayList> initList = (HashMap<String, ArrayList>) message.getMessageObject();
            players = initList.get("Players");
            tiles = initList.get("Tiles");
            pathCards = initList.get("PathCards");
            deck = initList.get("Deck");

            for (Player player : players) {
                player.applyColor();
                if (player.getPlayerID() == localPlayer.getPlayerID()) {
                    this.localPlayer = player;
                }
            }
        }

        this.readCards();
    }

    private void readCards() {

        listCardImages = new Hashtable<>();

        File folder = new File("src/ch/atlantis/res/Spielmaterial/");

        if (!folder.isDirectory()) {
            //TODO:Error blabla
        }

        File[] myFiles = folder.listFiles();

        if (myFiles != null) {
            for (File file : myFiles) {
                if (file.exists() && file.isFile()) {
                    if (file.getName().endsWith(".jpg")){

                        //without the substring(4) the path is invalid resp nullpointerexception
                        ImageView imageView = new ImageView(new Image(file.getPath().substring(4)));
                        listCardImages.put(file.getName(), imageView);
                    }
                }
            }
        }
    }


    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public ArrayList<Card> getPathCards() {
        return pathCards;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public Hashtable<String, ImageView> getListCardImages() {return listCardImages; }

}
