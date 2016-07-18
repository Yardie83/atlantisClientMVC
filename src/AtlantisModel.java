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
    private SimpleStringProperty chatString = new SimpleStringProperty();

    public AtlantisModel() {
    }

    public void connectToServer() {

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
        if (socket == null || socket.isClosed() ) {
            connectToServer();
        } else {
            outWriter.println(text);
            outWriter.flush();
        }
    }

    private void receiveMessage() {

        Task receiveMessageTask = new Task() {
            @Override
            protected Object call() throws Exception {
                while (!isCancelled()) {
                    System.out.println("Task started");
                    try {
                        String response;
                        while (true) {
                            if (socket == null || socket.isClosed()) {
                                System.out.println("Connecting to Server");
                                connectToServer();
                            } else {
                                response = inReader.readLine();
                                System.out.println("Server -> " + response);
                                updateChatString(response);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Could not get response from host");
                    }
                    break;
                } // End While
                return null;
            }
        };
        Thread receiverThread = new Thread(receiveMessageTask);
        receiverThread.start();
    }

    private void updateChatString(String response) {
        chatString.setValue(response);
    }

    public SimpleStringProperty getChatString() {
        return chatString;
    }
}
