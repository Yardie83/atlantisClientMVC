package ch.atlantis.model;

import ch.atlantis.util.Language;
import ch.atlantis.util.Message;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 *
 * Main model class of the Atlantis client program.
 *
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

    private ObservableList<String> gameList;

    private ArrayList<Language> languageList;
    private String selectedLanguage;

    private boolean autoConnect = true;

    private SimpleStringProperty chatString;
    private SimpleStringProperty connectionStatus;
    private SimpleIntegerProperty createProfileSuccess;
    private SimpleIntegerProperty loginSuccess;
    private SimpleStringProperty userName;
    private boolean autoConnect = true;
    private Thread clientTask;
    private ObservableList<String> gameList;
    private ArrayList<Language> languageList;
    private String currentLanguage;

    public AtlantisModel() {
        chatString = new SimpleStringProperty();
        connectionStatus = new SimpleStringProperty();
        createProfileSuccess = new SimpleIntegerProperty(0);
        loginSuccess = new SimpleIntegerProperty(0);
        userName = new SimpleStringProperty();
        gameList = FXCollections.observableArrayList();
        //TODO: (loris) read the config file here
        soundControler();
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
            chatString.setValue(LocalDateTime.now()+" Connecting to Server...");
            try {
                socket = new Socket(HOST, PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inReader = new ObjectInputStream(socket.getInputStream());
                receiveMessage();
            } catch (IOException e) {
                System.err.println("Connection to the server failed!\nPlease check if the server is running");
                chatString.setValue(LocalDateTime.now()+ " Connection to the server failed!\nPlease check if the server is running");
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
                chatString.setValue(LocalDateTime.now()+" Connected to Server\nWaiting for incoming messages");
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
                                case NEWGAME:
                                    //Add code here
                                    break;
                                case GAMELIST:
                                    handleGameList(message);
                                    break;
                                case USERNAME:
                                    handleUserName(message);
                                    break;
                                case LANGUAGELIST:
                                    handleLanguages(message);
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

    private void handleGameList(Message message) {
        gameList.add(message.getMessageObject().toString());
    }

    private void handleLanguages(Message message) {

        languageList = (ArrayList<Language>) message.getMessageObject();

        if (languageList == null && languageList.size() == 0) {
            System.out.println("Error no language could be found");
            return;
        }
        else {
            //TODO: (Loris) Config file stuff
            currentLanguage = languageList.get(0).getCulture();
            System.out.println("THE SELECTED LANGUAGE IS: " + currentLanguage);
        }
    }

    private void handleUserName(Message message) {
        userName.setValue(message.getMessageObject().toString());
    }

    private void handleCreateProfile(Message message) {
        System.out.println();
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

    public void sendMessage(Message message) {
        if ((socket == null || socket.isClosed()) && autoConnect) {
            connectToServer();
            autoConnect = false;
            sendMessage(message);
        } else if (!autoConnect) {
            chatString.setValue(LocalDateTime.now()+" Maximum connection attempts reached.");
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
     *
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

    private void soundControler() {

        Media backgroundMusic = new Media(Paths.get("src/ch/atlantis/res/Maid with the Flaxen Hair.mp3").toUri().toString());

        MediaPlayer myPlayer = new MediaPlayer(backgroundMusic);
        myPlayer.play();
    }

    public Language getSelectetLanguage(String culture) {

        for (Language language : this.getLanguageList()) {

            if (language.getCulture().equals(culture)) {

                return language;
            }
        }
        return null;
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

    public ObservableList<String> getGameList() {
        return gameList;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    //Language Getters and Setters
    public ArrayList<Language> getLanguageList() {
        return languageList;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

}