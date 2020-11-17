package multiminesweeper.server.relay;

import java.io.IOException;
import java.io.UTFDataFormatException;

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
            server.unexpectedClose(client);
        }
    }

    public void loop() throws IOException {
        while (true) {
            if (client.closed) return;
            try {
                String message = client.inputStream.readUTF();
                // client asked us to close the connection
                if (message.startsWith("system:")) {
                    if (message.startsWith("system:close")) {
                        server.requestedClose(client);
                    }
                } else if (message.startsWith("system:tryFindPartner")) {
                    boolean result = server.findPartner(client);
                    server.sendResult(client, result);
                } else {
                    server.sendToOther(client, message);
                }
            } catch (UTFDataFormatException data) {
                client.sendMessage("system", "badMessage");
            }
        }
    }
}