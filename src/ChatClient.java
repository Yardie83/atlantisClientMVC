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
    private Socket socket;
    private final String serverAddress = "localhost";
    private String line;

    public ChatClient(MainController mainController) throws IOException {
        this.mainController = mainController;

        try {
            socket = new Socket(serverAddress, 9000);
            receiveChatMessage();
        } catch (IOException e){
            System.out.println("Connection to Server Failed");
        }

    }


    private void receiveChatMessage() throws IOException {

        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        // Process all messages from server, according to the protocol.
        while (true) {
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

    public void sendChatMessage(String text) throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        out.append(text);
        out.println();
        out.flush();
    }
}
