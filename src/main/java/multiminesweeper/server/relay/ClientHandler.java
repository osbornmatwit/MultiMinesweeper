package multiminesweeper.server.relay;

import multiminesweeper.message.*;

import java.io.IOException;
import java.net.SocketException;

class ClientHandler implements Runnable {
    public Thread thread;
    private final RelayServer server;
    private final Client client;
    public boolean logActivity = false;

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
        } catch (SocketException ex) {
            server.unexpectedClose(client, "Socket closed");
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

            logMessage("waiting for object");
            Message outerMessage = client.getMessage();

            logMessage(outerMessage.toString());

            // client asked us to close the connection
            if (outerMessage instanceof DisconnectMessage) {
                DisconnectMessage message = (DisconnectMessage) outerMessage;
                server.requestedClose(client, message.reason);
            } else if (outerMessage instanceof ConnectionRequestMessage) {
                ConnectionRequestMessage request = (ConnectionRequestMessage) outerMessage;
                // Set client requirements here
                client.password = request.password;
                client.setMetadata("name", request.name);

                boolean result = server.findPartner(client, request.blocking);
                if (!request.blocking) {
                    server.sendResult(client, "connection request", result);
                } else {
                    // a client that wants to wait for another
                    server.waitForPartner(client);
                }
            } else if (outerMessage instanceof InfoChangeMessage) {
                InfoChangeMessage message = (InfoChangeMessage) outerMessage;
                client.setMetadata(message.property, message.value);
            } else if (outerMessage instanceof QueryMessage) {
                String result = client.getMetadata(((QueryMessage) outerMessage).property);
                // RESULT CAN BE NULL
                client.sendResult("QueryResult", result);
            } else {
                server.sendToOther(client, outerMessage);
            }
        }
    }

    private void logMessage(String message) {
        if (logActivity) {
            System.out.println(client.toString() + ": " + message);
        }
    }
}