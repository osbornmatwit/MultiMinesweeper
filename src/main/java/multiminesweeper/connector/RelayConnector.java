package multiminesweeper.connector;

import multiminesweeper.message.Message;
import multiminesweeper.message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Connects the client to another through a server, with the server always acting in between the clients
 */
public class RelayConnector extends AbstractConnector {
    private boolean hasPartner = false;
    private boolean closed = false;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;


    public RelayConnector(String host, int port) throws IOException {
        socket = new Socket(host, port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void handleMessage(Message message) {

    }

    public Message getMessage() throws IOException {
        String response = inputStream.readUTF();
        return new Message(response);
    }

    @Override
    public boolean tryFindPartner() {
        try {
            outputStream.writeUTF("server:tryFindPartner");
            Message response = getMessage();
            if (response.type != MessageType.RESULT) throw new RuntimeException("This shouldn't happen");
            return Boolean.parseBoolean(response.parts[1]);
        } catch (IOException ignored) {
        }
        return false;
    }

    @Override
    public void waitForPartner() {

    }

    @Override
    public boolean hasPartner() {
        return hasPartner;
    }

    @Override
    public boolean hasClosed() {
        return closed;
    }

    public void close() {
        closed = true;
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
