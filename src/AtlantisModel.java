import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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
    private SimpleStringProperty chatString = new SimpleStringProperty();
    private SimpleStringProperty connectionStatus = new SimpleStringProperty();
    private boolean autoConnect = true;
    private Thread clientTask;

    public AtlantisModel() {
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
                System.err.println("Connection to the server failed. Please check if the server is running");
                chatString.setValue("Connection to the server failed. Please check if the server is running");
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

                                //TODO: Implement here the same switch statement as in the server for each messageType

                                switch (message.getMessageType()) {

                                    case DISCONNECT:
                                        //Add code here
                                        break;

                                    case CHAT:
                                        handleChatMessage(message);
                                        break;

                                    case CREATEPROFILE:
                                        //Add code here
                                        break;

                                    case LOGIN:
                                        //Add code here
                                        break;

                                    case NEWGAME:
                                        //Add code here
                                        break;

                                    case GAMELIST:
                                        //Add code here
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
                        }
                    }
                return null;
            }
        };
        clientTask = new Thread(receiveMessageTask);
        clientTask.start();
    }

    private void handleChatMessage(Message message) {
        chatString.setValue(message.getMessage().toString());
        chatString.setValue("");
        System.out.println("Server -> " + message.getMessage().toString());
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
                System.out.println("Sending to Server -> " + message.getMessage());
                outputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                sendMessage(new Message(MessageType.DISCONNECT, "Closing"));
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

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }


//TODO: Make this better-looking instead of a PDF create a view with the rules
    public void showGameRules() {
        try {
            File file = new File(getClass().getResource("/res/Atlantis_Spielregel.pdf").getFile());

            if (file.exists()){
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}