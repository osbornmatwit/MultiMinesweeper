package multiminesweeper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket connectionSocket = new Socket("localhost", 1234);

        @SuppressWarnings("resource")
        DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        System.out.println("Enter Request: ");
        // GET /HelloWorld.html HTTP/1.1
        Scanner input = new Scanner(System.in);
        String message = input.nextLine();

        outToServer.writeBytes(message + "\r\n");

        // get the response from

        while (inFromServer.readLine() != null) {
            String serverMessage = inFromServer.readLine();
            System.out.println(serverMessage);
        }

        outToServer.close();
        connectionSocket.close();


    }
}
