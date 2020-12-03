package multiminesweeper.server.relay;

import multiminesweeper.message.ConnectionMessage;
import multiminesweeper.message.Message;
import multiminesweeper.message.result.BooleanResultMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
// TODO: Handle SocketErrors more gracefully
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
        Runtime.getRuntime().addShutdownHook(new Thread(server::closeAll));
        server.listen(port);
    }

    private void closeAll() {
        for (var client : clients) {
            requestedClose(client, "Server Closed");
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.err.println("Failed to open socket");
            return;
        }
        System.out.println("Server listening on port " + port);
        while (true) {
            Client client;
            try {
                Socket newSocket = serverSocket.accept();
                System.out.println("Client connected");
                client = createClient(newSocket);
                System.out.println("Client accepted");
            } catch (IOException ex) {
                System.err.println("Error Connecting to client");
                continue;
            }
            new Thread(new ClientHandler(this, client), client.toString()).start();
        }

    }

    /**
     * Get a client by its' uuid
     * @param uuid UUID to get client for
     * @return Client, if one is found, {@code null} if not.
     */
    public Client getClientByUUID(UUID uuid) {
        return uuidClientMap.get(uuid);
    }

    /**
     * Create a client from a socket connection
     * @param clientConnection The connection to create a client from
     * @throws IOException If creating the client fails, always before it's added to any data structures
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
        // find a partner that matches client requirements (see Client.filterRequirements)

        // Returns an Optional containing nothing or a client matching the requirements
        Optional<Client> maybePartner = waitingList.stream().filter(client::filterRequirements).findFirst();

        if (maybePartner.isPresent()) {
            Client partner = maybePartner.get();
            waitingList.remove(partner);
            waitingList.remove(client);
            pairClients(client, partner);
            return true;
        } else {
            return false;
        }
    }

    public void waitForPartner(Client client) throws IOException {
        waitingList.add(client);
        findPartner(client);
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
