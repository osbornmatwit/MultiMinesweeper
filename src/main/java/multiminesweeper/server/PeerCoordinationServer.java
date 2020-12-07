package multiminesweeper.server;

import multiminesweeper.message.ConnectionMessage;
import multiminesweeper.message.ConnectionRequestMessage;
import multiminesweeper.message.ErrorMessage;
import multiminesweeper.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * Connects peers together
 */
public class PeerCoordinationServer {
    ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        PeerCoordinationServer server = new PeerCoordinationServer();
        int port = 8080;
        Runtime.getRuntime().addShutdownHook(new Thread(server::closeAll));
    }

    public boolean tryFindPair(Client client) {
        var possibleClient = clients.stream().filter(client::filterRequirements).findFirst();
        // TODO: Pair clients together

        return possibleClient.isPresent();
    }

    public void waitForPair(Client client) {
    }

    public void closeAll() {
        for (var client : clients) {
            client.close();
        }
    }

    public void remove(Client client) {
        clients.remove(client);
    }
}

class Client {
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    public SocketAddress address;
    public String name;
    public String password;
    public boolean connected = true;


    public Client(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        address = socket.getRemoteSocketAddress();
    }

    public void sendMessage(Message message) throws IOException {
        outputStream.writeObject(message);
    }

    public Message getMessage() throws IOException {
        try {
            return (Message) inputStream.readObject();
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Invalid message received");
        }
    }

    private void pair(Client other, boolean host) throws IOException {
        ConnectionMessage message = new ConnectionMessage(other.address, other.name, host);
        outputStream.writeObject(message);
        close();
    }

    public void close() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Connection error while closing connection");
        }
        connected = false;
    }

    public static void pairClients(Client client1, Client client2) {
        try {
            client1.pair(client2, true);
            client2.pair(client1, false);
        } catch (IOException ex) {
            client1.close();
            client2.close();
        }
    }

    public boolean filterRequirements(Client other) {
        return this != other && password.equals(other.password);
    }

}

class ClientHandler implements Runnable {
    public final Client client;
    public final PeerCoordinationServer server;

    public ClientHandler(PeerCoordinationServer server, Client client) {
        this.server = server;
        this.client = client;

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            loop();
        } catch (IOException ex) {
            client.close();
            server.remove(client);
        }
    }

    public void loop() throws IOException {
        while (client.connected) {
            Message message = client.getMessage();
            if (message instanceof ConnectionRequestMessage) {
                ConnectionRequestMessage connectionMessage = (ConnectionRequestMessage) message;
                client.name = connectionMessage.name;
                client.password = connectionMessage.password;
                if (connectionMessage.blocking) {
                    server.waitForPair(client);
                } else {
                    server.tryFindPair(client);
                }
            } else {
                client.sendMessage(new ErrorMessage("Can only send connection requests to this server"));
                client.close();
            }
        }
    }
}