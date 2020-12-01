package multiminesweeper.server.relay;

import multiminesweeper.connector.RelayConnector;
import multiminesweeper.connector.events.EventType;

import java.io.IOException;

class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("hey!");
        RelayConnector connector = new RelayConnector("localhost", 8080);

        connector.addEventListener(EventType.CHAT, (event) -> System.out.println("Chat message: " + event.data));

        Thread thread = new Thread(connector::waitForPartner);
        thread.start();
        thread.join();
        System.out.println("Connected to partner");

        new Thread(connector).start();

        connector.sendChat("wow, so cool");
    }
}