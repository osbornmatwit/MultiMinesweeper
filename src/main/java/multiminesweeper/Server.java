package multiminesweeper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    static class Client implements Runnable {
        Socket connectionSocket;


        public Client(Socket socket) {
            this.connectionSocket = socket;
        }

        @Override
        public void run() {
            // DONE
            try {
                process();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        private void process() throws IOException {

            // Get a reference to the socket's input and output streams.
            InputStream is = connectionSocket.getInputStream() ;
            DataOutputStream outToClient = new DataOutputStream( connectionSocket.getOutputStream() ) ;

            // Set up input stream filters.
            @SuppressWarnings( "resource" )
            BufferedReader inFromClient = new BufferedReader( new InputStreamReader( is ) ) ;

            // Get the request line of the HTTP request message.
            String clientMessage = inFromClient.readLine() ;

            // Received message
            System.out.println( "RECEIVED: " + clientMessage ) ;

            // Create our array
            String[] list = clientMessage.split( " " ) ;

            // File Name
            String fileName = list[ 1 ].substring( 1 ) ;

            try {

                File file = new File(fileName);
                Scanner sc = new Scanner(file);
                outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
                outToClient.writeBytes("Content-Type: text/html\r\n\r\n");

                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    outToClient.writeBytes(str + "\r\n");
                }// end while

            }// end try
            catch (Exception e) {
                File error = new File("C:\\Users\\padillah\\OneDrive - Wentworth Institute of Technology\\Eclipse Workspaces\\Sophmore Year\\Web Server\\Error.html");
                Scanner sc = new Scanner(error);
                outToClient.writeBytes("HTTP/1.1 404 Not Found\r\n");
                outToClient.writeBytes("Content-Type: text/html\r\n\r\n");

                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    outToClient.writeBytes(str + "\r\n");
                }// end while
            }// end catch


        }
    }


    public static void main() throws Exception {
        @SuppressWarnings("resource")
        ServerSocket listenSocket = new ServerSocket(1234);

        System.out.println("This  is ready to receive");


        //noinspection InfiniteLoopStatement
        while (true) {

            @SuppressWarnings("resource")
            Socket connectionSocket = listenSocket.accept();

            // Step 2
            // Create a thread object

            Client client = new Client(connectionSocket);

            //instantiate a thread object
            Thread thread = new Thread(client);

            //step 3
            thread.start();

        }
    }
}
