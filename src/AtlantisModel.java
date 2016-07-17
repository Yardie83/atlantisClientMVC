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

    public AtlantisModel() {
    }

    public void connectToServer() {

        try{
            socket = new Socket(HOST, PORT);
            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        }catch (IOException e){
            System.err.println("connection failed");
        }
    }
}
