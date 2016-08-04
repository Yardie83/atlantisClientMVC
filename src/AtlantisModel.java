import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
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

    public AtlantisModel() {
    }

    public void connectToServer() {

        if (socket != null && !socket.isClosed()) {
            closeConnection();
        }
        if (autoConnect) {
            System.out.println("Connecting to Server.");
            chatString.setValue("Connecting to Server.");
            connectionStatus.setValue("Connecting");
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
        } else {
            return;
        }
    }

    private void receiveMessage() {

        Task receiveMessageTask = new Task() {
            @Override
            protected Object call() throws Exception {
                while (autoConnect) {
                    System.out.println("Connected to Server\nWaiting for incoming messages");
                    chatString.setValue("Connected to Server\nWaiting for incoming messages");
                    connectionStatus.setValue("Connected");
                    while (true) {
                        try {
                            if ((socket == null || socket.isClosed())) {
                                connectToServer();
                            } else {
                                message = (Message) inReader.readObject();
                                // TODO: When the user types two times the same message it does not show up because there is no "change" registered in the AtlantisController.
                                chatString.setValue(message.getMessage().toString());
                                System.out.println("Server -> " + message.getMessage().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } return null;
            }
        };
        new Thread(receiveMessageTask).start();
    }

    public void sendMessage(Message message) {
        if ((socket == null || socket.isClosed()) && autoConnect == true) {
            connectToServer();
            autoConnect = false;
            sendMessage(message);
        } else if ((socket == null || socket.isClosed()) && autoConnect == false) {
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
                sendMessage(new Message(MessageType.DISCONNECT));
                autoConnect = false;
                inReader.close();
                outputStream.close();
                socket.close();
                System.out.println("Connection to the server closed");
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

    public void autoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

}
