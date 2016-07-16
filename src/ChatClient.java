import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Hermann Grieder on 15.07.2016.
 *
 */
public class ChatClient {

    private MainController mainController;
    private BufferedReader in;
    private PrintWriter out;
    private final String serverAddress = "localhost";
    private String line = null;

    public ChatClient(MainController mainController) throws IOException {
        this.mainController = mainController;
    }
}
