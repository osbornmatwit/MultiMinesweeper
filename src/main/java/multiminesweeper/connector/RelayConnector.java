package multiminesweeper.connector;

import multiminesweeper.message.*;
import multiminesweeper.message.result.BooleanResultMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Connects the client to another through a server, with the server always acting in between the clients
 */
public class RelayConnector extends AbstractConnector implements Runnable {
    private boolean hasPartner = false;
    private boolean closed = false;
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;


    public RelayConnector(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            loop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void loop() throws IOException {
        while (true) {
            Message message;
            synchronized (inputStream) {
                message = getMessage();
            }
            handleMessage(message);
        }
    }

    public void handleMessage(Message message) {
        // TODO: Make version of this method on AbstractConnector,
        // and create abstract methods for the various tasks like handling a new connection
        switch (message.type) {
            // handle any logging or special actions
            case DISCONNECT:
                System.err.printf("Disconnected: %s%n", message);
                sendEvent(message);
                break;
            case ERROR:
                System.err.printf("Error message: %s%n", message);
                sendEvent(message);
                break;
            case INIT_CONNECTION:
                System.out.println(message);
                sendEvent(message);
                break;
            case CHAT:
                System.out.println("Chat message: " + message);
                sendEvent(message);
                break;
            case INFO_QUERY:
            case MOVE:
                throw new UnsupportedOperationException("Haven't coded this part yet ;)");
            case RESULT:
                // shouldn't be caught by generic handler, should be caught by whoever used it
                System.err.println("Unexpected result");
                close();
                break;
            case CONNECTION_REQUEST:
                System.err.println("Server event received on client");
                break;
        }
    }

    public Message getMessage() throws IOException {
        try {
            Message message = (Message) inputStream.readObject();
            System.out.println(message);
            return message;
        } catch (ClassNotFoundException ex) {
            // rethrow as runtime exception, since we control both ends of this connection
            // and shouldn't send anything other than a message
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean tryFindPartner() {
        // TODO: Refactor this so that instead of waiting for a response, these methods simply trigger the server to send a connection request
        // lock the input stream so that this is the only thing waiting for a response
        synchronized (inputStream) {
            try {
                sendObject(new ConnectionRequestMessage());
                Message response = getMessage();
                if (!(response instanceof BooleanResultMessage)) {
                    // maybe change this to run event handler instead
                    throw new RuntimeException("Didn't get boolean response from client request");
                }
                boolean result = ((BooleanResultMessage) response).result;
                if (result) {
                    hasPartner = true;
                }
                return result;
            } catch (IOException ignored) {
            }
            return false;
        }
    }

    @Override
    public void waitForPartner() {
        synchronized (inputStream) {
            try {
                sendObject(new ConnectionRequestMessage(true));
                while (!hasPartner) {
                    Message message = getMessage();
                    if (message instanceof ConnectionMessage) {
                        hasPartner = true;
                    } else {
                        handleMessage(message);
                    }
                }
            } catch (IOException ex) {
                close();
            }
        }
    }

    @Override
    public boolean hasPartner() {
        return hasPartner;
    }

    @Override
    public boolean hasClosed() {
        return closed;
    }

    @Override
    public void sendChat(String message) throws IOException {
        sendObject(new StringMessage(MessageType.CHAT, message));
    }

    private void sendObject(Object object) throws IOException {
        System.out.println("Writing message");
        synchronized (outputStream) {
            outputStream.writeObject(object);
        }
    }

    public void close() {
        closed = true;
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
