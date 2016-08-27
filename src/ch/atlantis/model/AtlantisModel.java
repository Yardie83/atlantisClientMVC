package ch.atlantis.model;

import ch.atlantis.util.Language;
import ch.atlantis.util.Message;
import ch.atlantis.util.MessageType;
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
import java.util.ArrayList;

/**
 * Created by Loris Grether and Hermann Grieder on 17.07.2016.
 */
public class AtlantisModel {

    private ObjectInputStream inReader;
    private ObjectOutputStream outputStream;
    private Message message;
    private Socket socket;
    private final String HOST = "127.0.0.1";
    private final int PORT = 9000;
    private SimpleStringProperty chatString;
    private SimpleStringProperty connectionStatus;
    private SimpleIntegerProperty createProfileSuccess;
    private SimpleIntegerProperty loginSuccess;
    private SimpleStringProperty userName;
    private boolean autoConnect = true;
    private Thread clientTask;
    private ObservableList<String> gameList;

    public AtlantisModel() {
        chatString = new SimpleStringProperty();
        connectionStatus = new SimpleStringProperty();
        createProfileSuccess = new SimpleIntegerProperty(0);
        loginSuccess = new SimpleIntegerProperty(0);
        userName = new SimpleStringProperty();
        gameList = FXCollections.observableArrayList();
    }

    public void connectToServer() {

        if (socket != null && !socket.isClosed()) {
            closeConnection();
        }
        if (autoConnect) {
            System.out.println("Connecting to Server...");
            chatString.setValue("Connecting to Server...");
            connectionStatus.setValue("Connecting...");
            try {
                socket = new Socket(HOST, PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inReader = new ObjectInputStream(socket.getInputStream());
                receiveMessage();
            } catch (IOException e) {
                System.err.println("Connection to the server failed!\nPlease check if the server is running");
                chatString.setValue("Connection to the server failed!\nPlease check if the server is running");
                connectionStatus.setValue("Disconnected");
            }
        }
    }

    private void receiveMessage() {

        Task receiveMessageTask = new Task() {
            @Override
            protected Object call() throws Exception {
                System.out.println("Connected to Server\nWaiting for incoming messages");
                chatString.setValue("Connected to Server\nWaiting for incoming messages");
                connectionStatus.setValue("Connected");
                while (autoConnect) {
                    try {
                        if ((socket == null || socket.isClosed())) {
                            connectToServer();
                        } else {
                            message = (Message) inReader.readObject();

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
                        //TODO: Ask Bradley if this is the correct way to solve this problem
                        System.out.println("Connection by server closed");
                        autoConnect = false;
                        //closeConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Server -> " + message.getMessageObject());
                    }
                }
                return null;
            }
        };
        clientTask = new Thread(receiveMessageTask);
        clientTask.start();
    }

    private void handleGameList(Message message) {
        gameList.add(message.getMessageObject().toString());
    }

    private void handleLanguages(Message message) {

        ArrayList<Language> languages = (ArrayList<Language>) message.getMessageObject();

        Language testlanguage = languages.get(0);

        System.out.println(testlanguage.getLanguageTable().values());

    }

    private void handleUserName(Message message) {
        String guestName = "Guest" + message.getMessageObject().toString();
        userName.setValue(guestName);
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
        chatString.setValue("");
    }

    public void sendMessage(Message message) {
        //TODO: This if statement can maybe be a do-while loop. We have to look into it
        if ((socket == null || socket.isClosed()) && autoConnect) {
            connectToServer();
            autoConnect = false;
            sendMessage(message);
        } else if ((socket == null || socket.isClosed()) && !autoConnect) {
            chatString.setValue("Maximum connection attempts reached.");
        } else {
            try {
                System.out.println("Sending to Server -> " + message.getMessageObject());
                outputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                sendMessage(new Message(MessageType.DISCONNECT, "Closing connection"));
                autoConnect = false;
                clientTask.interrupt();
                inReader.close();
                outputStream.close();
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Could not close connection to the server");
            e.printStackTrace();
        }
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
}