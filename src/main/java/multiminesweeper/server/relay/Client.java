package multiminesweeper.server.relay;

import multiminesweeper.message.DisconnectMessage;
import multiminesweeper.message.ErrorMessage;
import multiminesweeper.message.Message;
import multiminesweeper.message.result.BooleanResultMessage;
import multiminesweeper.message.result.StringResultMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class Client {
    public Socket socket;
    public final ObjectInputStream inputStream;
    public final ObjectOutputStream outputStream;
    public UUID uuid;
    public boolean paired = false;
    public volatile boolean closed = false;

    public Client(UUID uuid, Socket clientSocket) throws IOException {
        // TODO: Use setObjectInputFilter on inputStream to proactively stop non-messages from being sent
        System.out.println("Creating Client");
        this.uuid = uuid;
        socket = clientSocket;
        // must create output stream first, otherwise it will deadlock
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        System.out.println("Client Created");
    }

    public Client(String uuid, Socket clientSocket) throws IOException {
        this(UUID.fromString(uuid), clientSocket);
    }

    public Client(Socket clientSocket) throws IOException {
        this(UUID.randomUUID(), clientSocket);
    }

    public void sendMessage(Message message) throws IOException {
        synchronized (outputStream) {
            outputStream.writeObject(message);
        }
    }

    public Message getMessage() throws IOException, ClassNotFoundException {
        synchronized (inputStream) {
            return (Message) inputStream.readObject();
        }
    }

    public void disconnect(String reason) {
        try {
            sendMessage(new DisconnectMessage(reason));
        } catch (IOException ignored) {
        }
        close();
    }

    public void sendError(String error) throws IOException {
        outputStream.writeObject(new ErrorMessage(error));
    }

    public void sendResult(String type, boolean result) throws IOException {
        sendMessage(new BooleanResultMessage(type, result));
    }

    public void sendResult(String type, String result) throws IOException {
        sendMessage(new StringResultMessage(type, result));
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
