import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Hermann Grieder on 16.07.2016.
 *
 */
public class Connection {

    AtlantisClient client;
    private static final int PORT = 9000;
    private static final String HOST = "127.0.0.1";
    private Socket clientSocket;
    private BufferedReader inReader;
    private PrintWriter outWriter;

    public Connection(AtlantisClient client) throws IOException {
        this.client = client;
        this.clientSocket = new Socket(HOST, PORT);
        System.out.println("Connected to: " + clientSocket.getRemoteSocketAddress());
        this.inReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.outWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        }


    public String receiveMessages() throws IOException {
        String incomingMessage;
        if ((incomingMessage = inReader.readLine()) != null){
            return incomingMessage;
        }return null;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public void send(String message) {
        outWriter.println(message);
    }
}
