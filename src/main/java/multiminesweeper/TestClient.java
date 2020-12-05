package multiminesweeper;

import multiminesweeper.connector.RelayConnector;
import multiminesweeper.connector.events.EventType;
import multiminesweeper.connector.events.MoveResult;

import java.io.IOException;
import java.util.Scanner;

class TestClient {
    static RelayConnector connector;
    static Scanner scanner;

    public static void main(String[] args) throws IOException {
        connector = new RelayConnector("localhost", 8080);

        System.out.println("Welcome to the test client!");
        scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        connector.setName(scanner.nextLine());

        connector.addEventListener(EventType.CHAT, event -> System.out.println("Chat message: " + event.data));
        connector.addEventListener(EventType.ERROR, event -> System.err.println("Error message: " + event.data));
        connector.setMoveHandler(move -> {
            System.out.println("Move: " + move);
            return MoveResult.HIT;
        });

        new Thread(connector).start();

        System.out.println("Connecting...");
        // async version of next line
//        Thread thread = new Thread(connector::waitForPartner);
//        thread.start();
//        thread.join();
        connector.waitForPartner();


        System.out.println("Connected to partner");

        boolean running = true;
        while (running) {
            String input = scanner.nextLine();
            String[] command = input.split(" ", 2);
            switch (command[0]) {
                case "chat":
                    if (command.length < 2) break;
                    connector.sendChat(command[1]);
                    break;
                case "exit":
                    running = false;
                    break;
                case "move":
                    runMove();
                    break;
            }
        }
    }

    static void runMove() throws IOException {
        System.out.print("Input x position: ");
        int x = scanner.nextInt();

        System.out.print("Input y position: ");
        int y = scanner.nextInt();

        System.out.print("Input flag state (true/false): ");
        boolean flag = scanner.nextBoolean();

        System.out.println("Sending move...");
        var result = connector.sendMove(new Move(x, y, flag));
        System.out.printf("Move result: %s%n", result);

    }
}