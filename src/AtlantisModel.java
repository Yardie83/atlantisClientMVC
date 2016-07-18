import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

/**
 * Created by LorisGrether and Hermann Grieder on 17.07.2016.
 */
public class AtlantisModel {

    BufferedReader inReader;
    PrintWriter outWriter;
    private Socket socket;
    private final String HOST = "127.0.0.1";
    private final int PORT = 9000;
    public SimpleStringProperty chatString = new SimpleStringProperty();

    public AtlantisModel() {
    }

    public void connectToServer() {

        if (socket != null && !socket.isClosed()) {
            closeConnection();
        }

        System.out.println("Connecting to Server");
        try {
            socket = new Socket(HOST, PORT);
            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            receiveMessage();
        } catch (IOException e) {
            System.err.println("Connection to the server failed. Please check if the server is running");
        }
    }

    public void sendMessage(String text) {
        if (socket == null || socket.isClosed()) {
            connectToServer();
            sendMessage(text);
        } else {
            if (text.equals("QUIT")) {
                closeConnection();
            } else {
                outWriter.println(text);
                outWriter.flush();
            }
        }
    }

    private void receiveMessage() {
        Task receiveMessageTask = new Task() {
            @Override
            protected Object call() throws Exception {
                while (!isCancelled()) {
                    System.out.println("Connected to Server\nWaiting for incoming messages");
                    try {
                        String response;
                        while (true) {
                            if (socket == null || socket.isClosed()) {
                                connectToServer();
                            } else {
                                response = inReader.readLine();
                                System.out.println("Server -> " + response);
                                chatString.setValue(response);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println();
                    } finally {
                        closeConnection();
                    }
                    break;
                } // End While
                return null;
            }
        };
        Thread receiverThread = new Thread(receiveMessageTask);
        receiverThread.start();
    }


    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Connection to server closed");
            }
        } catch (IOException e) {
            System.out.println("Could not close connection");
            e.printStackTrace();
        }
    }

    public SimpleStringProperty getChatString() {
        return chatString;
    }

}
