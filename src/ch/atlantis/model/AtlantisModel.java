package ch.atlantis.model;

import ch.atlantis.game.Game;
import ch.atlantis.AtlantisClient;
import ch.atlantis.game.*;
import ch.atlantis.util.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 * <p>
 * Main model class of the Atlantis client program.
 * <p>
 * Handles the connection to the server.
 * Sends messages from the client to the server.
 * Receives messages from the server.
 * Handles them according to their MessageType.
 * Closes the connection on close of the application.
 */
public class AtlantisModel {

    private Logger logger;

    private ObjectInputStream inReader;
    private ObjectOutputStream outputStream;
    private Message message;
    private Socket socket;

    private SimpleStringProperty chatString;
    private SimpleStringProperty connectionStatus;
    private SimpleIntegerProperty createProfileSuccess;
    private SimpleIntegerProperty loginSuccess;
    private SimpleBooleanProperty gameReady;
    private SimpleBooleanProperty gameInfo;
    private SimpleStringProperty userName;
    private boolean autoConnect = true;
    private ObservableList<String> gameList;
    private LanguageHandler languageHandler;

    private Player player;
    private String currentLanguage;
    private Player localPlayer;
    private boolean isMusic = true;

    private AtlantisConfig conf;
    private Game game;

    private Music musicThread;

    private GamePiece gamePiece;
    private Card cardBehind;



    public AtlantisModel() {

        logger = Logger.getLogger(AtlantisClient.class.getName());

        chatString = new SimpleStringProperty();
        connectionStatus = new SimpleStringProperty();
        createProfileSuccess = new SimpleIntegerProperty(0);
        loginSuccess = new SimpleIntegerProperty(0);
        userName = new SimpleStringProperty();
        gameReady = new SimpleBooleanProperty(false);
        gameReady = new SimpleBooleanProperty(false);
        gameInfo = new SimpleBooleanProperty(false);
        gameList = FXCollections.observableArrayList();

        this.handleLanguages();
        this.handleSettings();
        this.soundController(conf.getIsMusic());
    }

    private void handleSettings() {

        if (conf == null) {
            conf = new AtlantisConfig();
            if (!conf.readAtlantisConfig()) {

                logger.warning("The AtlantisConfig could not be read!");
            }
        }
    }

    /**
     * Tries to connect to the server. If a connection could be established
     * the program then waits for incoming messages from the server.
     * In case of an error the user is informed in the Chat text area and the Status Bar
     * <p>
     * Hermann Grieder
     */

    public void connectToServer() {

        // For real server connection use IP: 138.68.77.135
        final String HOST = "127.0.0.1";
        final int PORT = 9000;

        if (socket != null && !socket.isClosed()) {
            closeConnection();
        }
        if (autoConnect) {
            chatString.setValue(LocalDateTime.now() + " Connecting to Server...");
            try {
                socket = new Socket(HOST, PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inReader = new ObjectInputStream(socket.getInputStream());
                receiveMessage();
            } catch (IOException e) {
                System.err.println("Connection to the server failed!\nPlease check if the server is running");
                chatString.setValue(LocalDateTime.now() + " Connection to the server failed!\nPlease check if the " +
                        "server is running");
                connectionStatus.setValue("Disconnected");
            }
        }
    }

    /**
     * Starts a new Task that receives messages from the server and
     * handles them according to their MessageType.
     * <p>
     * Hermann Grieder
     */
    private void receiveMessage() {

        Task receiveMessageTask = new Task() {
            @Override
            protected Object call() throws Exception {
                System.out.println("Connected to Server\nWaiting for incoming messages");
                chatString.setValue(LocalDateTime.now() + " Connected to Server\nWaiting for incoming messages");
                connectionStatus.setValue("Connected");
                while (autoConnect) {
                    try {
                        if ((socket == null || socket.isClosed())) {
                            connectToServer();
                        } else {
                            message = (Message) inReader.readObject();
                            System.out.println("Server -> " + message.getMessageObject());
                            switch (message.getMessageType()) {

                                case DISCONNECT:
                                    //Add code here
                                    break;
                                case CHAT:
                                    handleChatMessage(message);
                                    break;
                                case CREATEPROFILE:
                                    handleCreateProfile(message);
                                    break;
                                case LOGIN:
                                    handleLogin(message);
                                    break;
                                case GAMELIST:
                                    handleGameList(message);
                                    break;
                                case USERNAME:
                                    handleUserName(message);
                                    break;
                                case JOINGAME:
                                    handelJoinGame(message);
                                    break;
                                case GAMEREADY:
                                    handleReadyGame(message);
                                    break;
                                case GAMEINIT:
                                    handleGameInit();
                                    break;
                                case GAMEHANDLING:
                                    handleGameEvent(message);
                                    break;
                            }
                        }
                    } catch (SocketException e) {
                        System.out.println("Connection closed by server");
                        closeConnection();
                        autoConnect = false;
                    } catch (Exception e) {
                        System.out.println("AtlantisModel: Error reading message");
                        e.printStackTrace();
                        closeConnection();
                    }
                }
                return null;
            }
        };
        Thread clientTask = new Thread(receiveMessageTask);
        clientTask.start();
    }

    private void handleReadyGame(Message message) {
        String[] gameInfo = splitMessage(message);
        String gameName = gameInfo[0];
        Boolean gameIsReady = Boolean.parseBoolean(gameInfo[1]);
        if (localPlayer != null) {
            if (gameName.equals(localPlayer.getGameName()) && localPlayer.getPlayerID() == 0) {
                if (gameIsReady) {
                    gameReady.set(true);
                } else {
                    gameReady.set(false);
                }
            }
        }
    }

    private void handleGameInit() {
        gameInfo.set(true);
    }

    private void handelJoinGame(Message message) {
        String[] info = splitMessage(message);
        int playerId = Integer.valueOf(info[0]);
        String gameName = info[1];
        localPlayer = new Player(playerId, gameName);
        localPlayer.setName(userName.getValue());
    }

    public void handleGameEvent(Message message) {

        if (message.getMessageObject() instanceof HashMap) {
            HashMap<String, Object> gameInfo = (HashMap<String, Object>) message.getMessageObject();
            this.gamePiece = (GamePiece) gameInfo.get("GamePiece");
            this.cardBehind = (Card) gameInfo.get("CardBehind");
        }

    }

    private void handleGameList(Message message) {
        gameList.add(message.getMessageObject().toString());
    }

    private void handleLanguages() {

        languageHandler = new LanguageHandler();

        if (languageHandler.getLanguageList().size() == 0 || languageHandler.getLanguageList() == null) {
            System.out.println("No languages available");
        } else {

            //success

            //TODO: Log languages etc
        }
    }

    private void handleUserName(Message message) {
        userName.setValue(message.getMessageObject().toString());
    }

    private void handleCreateProfile(Message message) {
        if (message.getMessageObject().equals(Boolean.TRUE)) {
            createProfileSuccess.setValue(1);
        } else {
            createProfileSuccess.setValue(2);
        }
    }

    private void handleLogin(Message message) {
        if (message.getMessageObject().equals(Boolean.TRUE)) {
            loginSuccess.setValue(1);
        } else {
            loginSuccess.setValue(2);
        }
    }

    private void handleChatMessage(Message message) {
        chatString.setValue(message.getMessageObject().toString());
    }

    public void joinGame(String listInfo) {
        String[] info = listInfo.split(" ");
        String gameName = info[0];

        sendMessage(new Message(MessageType.JOINGAME, gameName));
    }

    public void sendMessage(Message message) {
        if ((socket == null || socket.isClosed()) && autoConnect) {
            connectToServer();
            autoConnect = false;
            sendMessage(message);
        } else if (!autoConnect) {
            chatString.setValue(LocalDateTime.now() + " Maximum connection attempts reached.");
        } else {
            try {
                System.out.println("Sending to Server -> " + message.getMessageObject());
                outputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the InputStreamReader, OutputStreamReader and the Socket.
     * <p>
     * Hermann Grieder
     */
    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                autoConnect = false;
                inReader.close();
                outputStream.close();
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Could not close connection to the server");
            e.printStackTrace();
        }
    }

    //TODO: Make this better-looking instead of a PDF create a view with the rules
    public void showGameRules() {
        try {
            File file = new File(getClass().getResource("/ch/atlantis/res/Atlantis_Spielregel.pdf").getFile());
            if (file.exists()) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void soundController(boolean status) {

        if (status) {
            musicThread = new Music();
            musicThread.start();

        } else if (musicThread != null && !status) {
            musicThread.stopMusic();
            musicThread.interrupt();
            musicThread = null;
        }
        this.setIsMusic(status);
    }

    public Language getSelectedLanguage(String culture) {

        for (Language language : this.getLanguageList()) {

            if (language.getCulture().equals(culture)) {

                return language;
            }
        }
        return null;
    }

    /**
     * Splits a message at the "," sign.
     *
     * @param message Message received from the client
     * @return String[]
     */
    private String[] splitMessage(Message message) {
        return message.getMessageObject().toString().split(",");
    }

    public Message getMessage() {
        return message;
    }

    public SimpleStringProperty getChatString() {
        return chatString;
    }

    public SimpleStringProperty getConnectionStatus() {
        return connectionStatus;
    }

    public SimpleIntegerProperty createProfileSuccessProperty() {
        return createProfileSuccess;
    }

    public SimpleIntegerProperty loginSuccessProperty() {
        return loginSuccess;
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public SimpleBooleanProperty gameReadyProperty() {
        return gameReady;
    }

    public SimpleBooleanProperty gameInfoProperty() {
        return gameInfo;
    }

    public ObservableList<String> getGameList() {
        return gameList;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public ArrayList<Language> getLanguageList() {
        return languageHandler.getLanguageList();
    }

    public String getConfigLanguage() {
        return this.conf.getConfigLanguage();
    }

    public void setIsMusic(boolean isMusic) {

        this.conf.setIsMusic(isMusic);
        this.conf.createAtlantisConfig();
    }

    public boolean getIsMusic() {
        return this.conf.getIsMusic();
    }

    public void setConfigLanguage(String currentLanguage) {

        this.conf.setConfigLanguage(currentLanguage);
        this.conf.createAtlantisConfig();
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public GamePiece getGamePiece() { return gamePiece; }

    public Card getCardBehind() { return cardBehind; }

}