package multiminesweeper.server.relay;

import multiminesweeper.message.ConnectionMessage;
import multiminesweeper.message.Message;
import multiminesweeper.message.result.BooleanResultMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Connects clients together and passes messages back and forth between them.
 */
public class RelayServer {
    ArrayList<Client> clients = new ArrayList<>();
    WeakHashMap<Client, Client> pairings = new WeakHashMap<>();
    WeakHashMap<UUID, Client> uuidClientMap = new WeakHashMap<>();
    ArrayList<Client> waitingList = new ArrayList<>();
    ServerSocket serverSocket;

    public static void main(String[] args) {
        RelayServer server = new RelayServer();
        int port = 8080;
        server.listen(port);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket newSocket = serverSocket.accept();
                System.out.println("Client connected");
                Client client = createClient(newSocket);
                System.out.println("Client accepted");
                new Thread(new ClientHandler(this, client)).start();
            }
        } catch (IOException ex) {
            System.err.println("Failed to open socket");
        }
    }

    /**
     * Get a client by it's uuid
     * @param uuid UUID to get client for
     * @return Client, if one is found, {@code null} if not.
     */
    public Client getClientByUUID(UUID uuid) {
        return uuidClientMap.get(uuid);
    }

    /**
     * Create a client from a socket connection
     * @param clientConnection The connection to create a client from
     * @throws IOException If creating the client fails.
     */
    public Client createClient(Socket clientConnection) throws IOException {
        Client newClient = new Client(clientConnection);
        uuidClientMap.put(newClient.uuid, newClient);
        return newClient;
    }

    private void pairClients(Client client1, Client client2) throws IOException {
        pairings.put(client1, client2);
        pairings.put(client2, client1);
        client1.paired = true;
        client2.paired = true;
        sendToPair(client1, new ConnectionMessage("test"));
    }

    public boolean findPartner(Client client) throws IOException {
        if (waitingList.size() < 1) {
            return false;
        } else {
            pairClients(client, waitingList.remove(0));
            return true;
        }
    }

    public void waitForPartner(Client client) throws IOException {
        if (findPartner(client)) {
            return;
        }
        waitingList.add(client);
    }

    /**
     * Gets the other part of a pair and sends both parts a message
     * @param client Either client in the pair
     */
    public void sendToPair(Client client, Message message) throws IOException {
        if (!client.paired) throw new IllegalArgumentException("Client was never paired");
        var otherClient = getPartner(client);
        if (otherClient == null) throw new IllegalStateException("Client has no pair");
        client.sendMessage(message);
        otherClient.sendMessage(message);
    }

    /**
     * Passes message from client to its' partner
     * @param sender The client sending the message
     * @param message The message
     * @throws IOException If message sending fails
     */
    public void sendToOther(Client sender, Message message) throws IOException {
        getPartner(sender).sendMessage(message);
    }

    public void sendResult(Client client, String resultLabel, boolean result) throws IOException {
        client.sendMessage(new BooleanResultMessage(resultLabel, result));
    }

    /**
     * When a client throws an IO error, this method gracefully removes it from the client list
     * and removes it's partner if it has one
     * @param client The client that closed unexpectedly or it's partner
     */
    public void unexpectedClose(Client client, String reason) {
        if (client.paired) {
            var otherClient = getPartner(client);
            try {
                // This means that some client disconnected, not the
                otherClient.disconnect(reason);
            } catch (Exception ignored) {
            }
            clients.remove(otherClient);
            waitingList.remove(otherClient);
        }
        try {
            // This means that some client disconnected, not the
            client.disconnect(reason);
        } catch (Exception ignored) {
        }
        clients.remove(client);
        waitingList.remove(client);
        client.close();
    }

    /**
     * client requested close
     * @param client The client that requested the close.
     */
    public void requestedClose(Client client, String reason) {
        if (client.paired) {
            var otherClient = getPartner(client);
            otherClient.disconnect(reason);
            clients.remove(otherClient);
        } else {
            waitingList.remove(client);
        }
        client.disconnect(reason);
        clients.remove(client);
    }

    public Client getPartner(Client client) {
        return pairings.get(client);
    }
}
