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
    private boolean autoConnect = true;

    public AtlantisModel() {
    }

    public void connectToServer() {

        if (socket != null && !socket.isClosed()) {
            closeConnection();
        }
        System.out.println("Connecting to Server.");
        try {
            socket = new Socket(HOST, PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inReader = new ObjectInputStream(socket.getInputStream());

            receiveMessage();
        } catch (IOException e) {
            System.err.println("Connection to the server failed. Please check if the server is running");
        }
    }

    private void receiveMessage() {
        Task receiveMessageTask = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    System.out.println("Connected to Server\nWaiting for incoming messages");
                    while (true) {
                        try {
                            if ((socket == null || socket.isClosed()) && autoConnect == true) {
                                connectToServer();
                            } else {
                                message = (Message) inReader.readObject();
                                chatString.setValue(message.getMessage().toString());
                                System.out.println("Server -> " + message.getMessage().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };
        new Thread(receiveMessageTask);
    }

    public void sendMessage(Message message) {
        if (socket == null || socket.isClosed()) {
            connectToServer();
            sendMessage(message);
        } else {
            try {
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

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

}
