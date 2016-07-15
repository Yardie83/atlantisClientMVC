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

    public void run() throws IOException{
        Socket socket = new Socket(serverAddress, 9000);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while((line = in.readLine()) != null){
            System.out.println(line);
            //mainController.receiveChatMessage(line);
        }
    }

    public void sendChatMessage(String text) throws IOException {
        out.append(text);
        out.println();
        out.flush();
    }

}
