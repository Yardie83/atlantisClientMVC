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
import java.lang.reflect.Array;
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

    private ObjectInputStream inReader;
    private ObjectOutputStream outputStream;
    private Message message;
    private Socket socket;
    private boolean autoConnect;

    private SimpleStringProperty chatString;
    private SimpleStringProperty connectionStatus;
    private SimpleIntegerProperty createProfileSuccess;
    private SimpleBooleanProperty gameOver;
    private SimpleIntegerProperty loginSuccess;
    private SimpleBooleanProperty moveValid;
    private SimpleBooleanProperty gameOverScores;
    private SimpleBooleanProperty gameReady;
    private SimpleBooleanProperty gameInfo;
    private SimpleStringProperty userName;
    private SimpleBooleanProperty givePurchasedCards;
    private SimpleBooleanProperty cardsForNotMoving;
    private SimpleBooleanProperty newTurn;
    private ObservableList<String> gameList;
    private LanguageHandler languageHandler;

    private int cumulatedGameTime;
    private int numberOfGames;

    private Player localPlayer;

    private AtlantisConfig conf;

    private Music musicThread;

    private Logger logger;

    public AtlantisModel() {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        chatString = new SimpleStringProperty();
        connectionStatus = new SimpleStringProperty();
        createProfileSuccess = new SimpleIntegerProperty(0);
        loginSuccess = new SimpleIntegerProperty(0);
        userName = new SimpleStringProperty();
        gameReady = new SimpleBooleanProperty(false);
        gameReady = new SimpleBooleanProperty(false);
        gameInfo = new SimpleBooleanProperty(false);
        gameList = FXCollections.observableArrayList();
        gameOver = new SimpleBooleanProperty(false);
        gameOverScores = new SimpleBooleanProperty(false);
        givePurchasedCards = new SimpleBooleanProperty(false);
        cardsForNotMoving = new SimpleBooleanProperty(false);
        newTurn = new SimpleBooleanProperty(false);
        autoConnect = true;

        this.handleLanguages();
        //the language can not be set here, because we first have to create the view respectively the controls
        this.handleSettings();
        this.soundController(conf.getIsMusic());
        moveValid = new SimpleBooleanProperty();
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
            chatString.setValue(LocalDateTime.now() + " Connecting to server...");
            try {
                socket = new Socket(HOST, PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inReader = new ObjectInputStream(socket.getInputStream());
                receiveMessage();
            } catch (IOException e) {
                logger.warning("Connection to the server failed. Check if the server is running.");
                chatString.setValue(LocalDateTime.now() + " Connection to the server failed. Check if the " +
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
                logger.info("Connected to server...waiting for incoming messages.");
                chatString.setValue(LocalDateTime.now() + " Connected to server...waiting for incoming messages.");
                connectionStatus.setValue("Connected.");
                while (autoConnect) {
                    try {
                        if ((socket == null || socket.isClosed())) {
                            connectToServer();
                        } else {
                            message = (Message) inReader.readObject();
                            logger.info("Server -> " + message.getMessageObject());
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
                                case MOVE:
                                    handleMove();
                                    break;
                                case GAMEOVER:
                                    handleGameOver(message);
                                    break;
                                case BUYCARD:
                                    handlePurchasedCards();
                                    break;
                                case CANTMOVE:
                                    handleCantMove();
                                    break;
                                case NEWTURN:
                                    handleNewTurn();
                                    break;
                                case PLAYERSTATS:
                                    updatePlayerStats(message);
                                    break;
                            }
                        }
                    } catch (SocketException e) {
                        logger.info("Connection closed by server.");
                        closeConnection();
                        autoConnect = false;
                    } catch (Exception e) {
                        logger.info("AtlantisModel -> Error reading message.");
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

    /**
     * Loris Grether
     *
     * @param message
     */
    private void updatePlayerStats(Message message) {

        int[] infos = (int[]) message.getMessageObject();

        cumulatedGameTime = infos[0];
        numberOfGames = infos[1];
    }

    /**
     * Hermann Grieder
     */
    private void handleNewTurn() {
        newTurn.setValue(true);
        newTurn.setValue(false);
    }

    /**
     * Fabian Witschi
     */
    private void handleCantMove() {
        cardsForNotMoving.setValue(true);
        cardsForNotMoving.setValue(false);
    }

    /**
     * Fabian Witschi
     */
    private void handlePurchasedCards() {
        givePurchasedCards.setValue(true);
        givePurchasedCards.setValue(false);
    }

    /**
     * Hermann Grieder
     * <br>
     * The incoming message tells us if the game is full on the server side and ready to be started.
     * If we are the player who created the game then we need to be informed that we can now start the game.
     * @param message The message received from the server
     */
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

    /**
     * Hermann Grieder
     * <br>
     * After the game is full on the server and we send the start message to the server the server sends us
     * this message back with the final information that we need to show the game. The gameLobbyController is
     * listening to this SimpleBooleanProperty.
     */
    private void handleGameInit() {
        gameInfo.set(true);
    }

    /**
     * Hermann Grieder
     * <br>
     *  After we join a game, the server sends us our playerId for that game specific game.
     *  We update this information in the localPlayer object.
     * @param message
     */
    private void handelJoinGame(Message message) {
        String[] info = splitMessage(message);
        int playerId = Integer.valueOf(info[0]);
        String gameName = info[1];
        localPlayer = new Player(playerId, gameName, userName.getValue());
    }

    /**
     * Hermann Grieder
     * <br>
     * Every time a move has been confirmed by the server we change the moveValid value to true so the game controller
     * can progress the game and update the to the new state of the game.
     */
    @SuppressWarnings("unchecked")
    public void handleMove() {
        moveValid.setValue(true);
        moveValid.setValue(false);
    }

    /**
     * Hermann Grieder
     * <br>
     * After every move the server checks if the game is over and informs all the players. Once the game is over
     * the game controller will show the game over screen.
     * @param message
     */
    private void handleGameOver(Message message) {
        if (message.getMessageObject() instanceof HashMap) {
            gameOverScores.setValue(true);
            gameOverScores.setValue(false);
        } else {
            Boolean isGameOver = (Boolean) message.getMessageObject();
            gameOver.set(isGameOver);
            logger.info("AtlantisModel -> GameOver: " + gameOver);
            gameOver.set(false);
        }
    }

    /**
     * Hermann Grieder
     * <br>
     * Every time a new game is created the server sends us the new gameList. We add all the games from the
     * message to the gameList observable list. The list is then updated in the view.
     * @param message
     */
    private void handleGameList(Message message) {
        gameList.add(message.getMessageObject().toString());
    }

    /**
     * Loris Grether
     * <br>
     * This method instantiate the language handler
     */

    private void handleLanguages() {
        languageHandler = new LanguageHandler();
        if (languageHandler.getLanguageList().size() == 0 || languageHandler.getLanguageList() == null) {
            logger.info("AtlantisModel -> No languages available.");
        } else {

            //success

            //TODO: Log languages etc
        }
    }

    /**
     * Loris Grether
     * <br>
     */
    private void handleSettings() {
        if (conf == null) {
            conf = new AtlantisConfig();
            if (!conf.readAtlantisConfig()) {
                logger.info("AtlantisConfig could not be read.");
            }
        }
    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the userName value of the SimpleStringProperty to the value in the incoming message.
     * @param message The incoming message object
     */
    private void handleUserName(Message message) {
        userName.setValue(message.getMessageObject().toString());
    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the createProfileSuccess SimpleIntegerProperty to 1 if the profile was successfully created otherwise
     * to 2
     *
     * @param message The incoming message object
     */
    private void handleCreateProfile(Message message) {
        if (message.getMessageObject().equals(Boolean.TRUE)) {
            createProfileSuccess.setValue(1);
        } else {
            createProfileSuccess.setValue(2);
        }
    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the loginSuccess SimpleIntegerProperty to 1 if the login was successful, otherwise to 2
     * @param message The incoming message object
     */
    private void handleLogin(Message message) {
        if (message.getMessageObject().equals(Boolean.TRUE)) {
            loginSuccess.setValue(1);
        } else {
            loginSuccess.setValue(2);
        }
    }

    /**
     * Hermann Grieder
     * <br>
     * Sets the value of the chatString SimpleStringProperty to the value in the incoming message.
     * @param message The incoming message object
     */
    private void handleChatMessage(Message message) {
        chatString.setValue(message.getMessageObject().toString());
    }

    public void joinGame(String listInfo) {
        String[] info = listInfo.split(" ");
        String gameName = info[0];
        sendMessage(new Message(MessageType.JOINGAME, gameName));
    }

    /**
     * Hermann Grieder
     * <br>
     * Sends a message to the outputStream in the current Thread
     * @param message The message object to be sent
     */
    public void sendMessage(Message message) {
        if ((socket == null || socket.isClosed()) && autoConnect) {
            connectToServer();
            autoConnect = false;
            sendMessage(message);
        } else {
            try {
                logger.info("Sending to server -> " + message.getMessageObject());
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                logger.info("Cannot send message to server");
            }
        }
    }

    /**
     * Hermann Grieder
     * <br>
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
            logger.info("Could not close the connection to the server.");
            e.printStackTrace();
        }
    }

    /**
     * Loris Grether
     * <br>
     *
     * @param status
     */
    public void soundController(boolean status) {

        if (status) {
            musicThread = new Music();
            musicThread.start();
        }

        if (musicThread != null && !status) {
            musicThread.stopMusic();
            musicThread.interrupt();
            musicThread = null;
        }
        this.conf.setIsMusic(status);
        this.conf.createAtlantisConfig();
    }

    /**
     * Loris Grether
     * <br>
     */
    public void showGameRules() {
        try {
            File file = new File("src/ch/atlantis/res/Atlantis_Spielregel.pdf");

            if (file.exists()) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ***************************** GETTERS & SETTERS **************************//

    public Language getSelectedLanguage(String culture) {
        for (Language language : this.getLanguageList()) {
            if (language.getCulture().equals(culture)) {
                return language;
            }
        }
        return null;
    }

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

    public SimpleBooleanProperty moveValidProperty() {
        return moveValid;
    }

    public SimpleBooleanProperty gameOverScores() { return gameOverScores; }

    public SimpleBooleanProperty givePurchasedCards() { return givePurchasedCards; }

    public SimpleBooleanProperty cardsForNotMoving() { return cardsForNotMoving; }

    public SimpleBooleanProperty newTurn() { return newTurn; }

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

    public boolean getIsMusic() {
        return this.conf.getIsMusic();
    }

    public void setConfigLanguage(String currentLanguage) {

        this.conf.setConfigLanguage(currentLanguage);
        this.conf.createAtlantisConfig();
    }

    public int getCumulatedGameTime() {
        return cumulatedGameTime;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public SimpleBooleanProperty gameOverProperty() {
        return gameOver;
    }
}