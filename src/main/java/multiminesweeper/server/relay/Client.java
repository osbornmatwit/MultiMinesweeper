package multiminesweeper.server.relay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client {
    public Socket socket;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public UUID uuid;
    public boolean paired = false;
    public volatile boolean closed = false;

    public Client(UUID uuid, Socket clientSocket) throws IOException {
        this.uuid = uuid;
        socket = clientSocket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public Client(String uuid, Socket clientSocket) throws IOException {
        this(UUID.fromString(uuid), clientSocket);
    }

    public Client(Socket clientSocket) throws IOException {
        this(UUID.randomUUID(), clientSocket);
    }

    public void sendMessage(String type, String data) throws IOException {
        String message = String.format("%s:%s", type, data);
        outputStream.writeUTF(message);
    }

    public void sendRawMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    public void close() {
        closed = true;
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
