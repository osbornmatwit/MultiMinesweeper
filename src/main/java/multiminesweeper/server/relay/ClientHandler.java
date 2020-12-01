package multiminesweeper.server.relay;

import multiminesweeper.message.ConnectionRequestMessage;
import multiminesweeper.message.DisconnectMessage;
import multiminesweeper.message.Message;

import java.io.IOException;

class ClientHandler implements Runnable {
    public Thread thread;
    private final RelayServer server;
    private final Client client;

    ClientHandler(RelayServer server, Client client) {
        this.server = server;
        this.client = client;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            loop();
        } catch (IOException ex) {
            server.unexpectedClose(client, "Network error");
        } catch (ClassNotFoundException ex) {
            System.err.printf("ClassNotFoundException in client connection: %s%n", ex);
            try {
                client.sendError("Invalid data sent");
            } catch (IOException ignored) {
            }
            server.unexpectedClose(client, "Misbehaving client");
        }
    }

    public void loop() throws IOException, ClassNotFoundException {
        while (true) {
            if (client.closed) return;

            System.out.println("waiting for object");
            Message outerMessage = client.getMessage();

            System.out.println(outerMessage);

            // client asked us to close the connection
            if (outerMessage instanceof DisconnectMessage) {
                DisconnectMessage message = (DisconnectMessage) outerMessage;
                server.requestedClose(client, message.reason);
            } else if (outerMessage instanceof ConnectionRequestMessage) {
                ConnectionRequestMessage request = (ConnectionRequestMessage) outerMessage;

                boolean result = server.findPartner(client);
                if (result || !request.blocking) {
                    server.sendResult(client, "connection request", result);
                } else {
                    // a client that wants to wait for another
                    server.waitForPartner(client);
                }
            } else {
                server.sendToOther(client, outerMessage);
            }
        }
    }
}