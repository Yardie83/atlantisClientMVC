import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by herma on 15.07.2016.
 *
 */
public class ChatClient{

    private MainController mainController;
    private BufferedReader in;
    private PrintWriter out;
    private final Socket socket;
    private final String serverAddress = "127.0.0.1";

    public ChatClient(MainController mainController) throws IOException {
        this.mainController = mainController;
        socket = new Socket(serverAddress, 9001);
        receiveChatMessage();
    }
    private void receiveChatMessage() throws IOException {

        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            mainController.receiveChatMessage(line);
//            if (line.startsWith("SUBMITNAME")) {
//                out.println(getName());
//            } else if (line.startsWith("NAMEACCEPTED")) {
//                textField.setEditable(true);
//            } else if (line.startsWith("MESSAGE")) {
//                messageArea.append(line.substring(8) + "\n");
//            }
        }
    }

    public void sendChatMessage() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
    }
}
