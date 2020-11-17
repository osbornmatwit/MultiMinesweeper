package multiminesweeper.server.relay;

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
        try {
            server.listen(port);
        } catch (IOException ex) {
            System.err.println(" failed to open socket");
            return;
        }
        System.out.println(" listening on port " + port);

    }

    public void listen(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Get a client by it's uuid
     * @param uuid UUID to get client for
     * @return multiminesweeper.Client, if one is found, {@code null} if not.
     */
    public Client getClientByUUID(UUID uuid) {
        return uuidClientMap.get(uuid);
    }

    /**
     * Create a client from a socket connection
     * @param clientConnection The connection to create a client from
     * @throws IOException If creating the client fails.
     */
    public void createClient(Socket clientConnection) throws IOException {
        Client newClient = new Client(clientConnection);
        uuidClientMap.put(newClient.uuid, newClient);
        waitingList.add(newClient);
    }

    private void pairClients(Client client1, Client client2) {
        pairings.put(client1, client2);
        pairings.put(client2, client1);
        client1.paired = true;
        client2.paired = true;
    }

    public boolean findPartner(Client client) {
        if (waitingList.size() < 2) {
            return false;
        } else {
            waitingList.remove(client);
            pairClients(client, waitingList.remove(0));
            return true;
        }
    }

    /**
     * Gets the other part of a pair and sends both parts a message
     * @param client Either client in the pair
     */
    public void sendToPair(Client client, String type, String data) throws IOException {
        if (!client.paired) throw new IllegalArgumentException("multiminesweeper.Client was never paired");
        var otherClient = getPartner(client);
        if (otherClient == null) throw new IllegalStateException("multiminesweeper.Client has no pair");
        client.sendMessage(type, data);
        otherClient.sendMessage(type, data);
    }

    /**
     * Passes message from client to its' partner
     * @param sender The client sending the message
     * @param message The message
     * @throws IOException If message sending fails
     */
    public void sendToOther(Client sender, String message) throws IOException {
        getPartner(sender).sendRawMessage(message);
    }

    public void sendResult(Client client, boolean result) throws IOException {
        client.sendMessage("result", Boolean.toString(result));
    }

    /**
     * When a client throws an IO error, this method gracefully removes it from the client list
     * and removes it's partner if it has one
     * @param client The client that closed unexpectedly or it's partner
     */
    public void unexpectedClose(Client client) {
        if (client.paired) {
            var otherClient = getPartner(client);
            try {
                // This means that some client disconnected, not the
                otherClient.sendMessage("system", "clientDisconnect");
            } catch (Exception ignored) {
            }
            clients.remove(otherClient);
            waitingList.remove(otherClient);
        }
        try {
            // This means that some client disconnected, not the
            client.sendMessage("system", "clientDisconnect");
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
    public void requestedClose(Client client) {
        if (client.paired) {
            var otherClient = getPartner(client);
            otherClient.close();
            clients.remove(otherClient);
        } else {
            waitingList.remove(client);
        }
        client.close();
        clients.remove(client);
    }

    public Client getPartner(Client client) {
        return pairings.get(client);
    }
}
