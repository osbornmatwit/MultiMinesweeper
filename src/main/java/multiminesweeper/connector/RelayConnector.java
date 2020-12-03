package multiminesweeper.connector;

import multiminesweeper.message.*;
import multiminesweeper.message.result.BooleanResultMessage;
import multiminesweeper.message.result.ResultMessage;
import multiminesweeper.message.result.StringResultMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Connects the client to another through a server, with the server always acting in between the clients
 */
public class RelayConnector extends AbstractConnector implements Runnable {
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    // Array blocking queue has to have capacity and will limit based on that
    private final BlockingQueue<ResultMessage> resultQueue = new LinkedBlockingQueue<>();
    private boolean hasPartner = false;
    private boolean closed = false;
    private ConnectionMessage connectionInfo;

    public RelayConnector(String host, int port) throws IOException {
        super("");
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
        synchronized (inputStream) {
            while (true) {
                Message message;
                message = getMessage();
                handleMessage(message);
                inputStream.notifyAll();
            }
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
                connectionInfo = (ConnectionMessage) message;
                sendEvent(message);
                break;
            case CHAT:
                System.out.println("Chat message: " + message);
                sendEvent(message);
                break;
            case RESULT:
                resultQueue.add((ResultMessage) message);
                break;
            case INFO_QUERY:
            case CHANGE_INFO:
                throw new UnsupportedOperationException("Info requests are handled by the server here");
            case MOVE:
                sendEvent(message);
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
    public String getPartnerName() throws IOException {
        return ((StringResultMessage) sendAndWait(new QueryMessage("name"))).result;
    }

    @Override
    public boolean tryFindPartner() {
        if (hasPartner) throw new IllegalStateException("Already have a partner!");
        try {
            Message response = sendAndWait(new ConnectionRequestMessage(getName()));
            if (!(response instanceof BooleanResultMessage)) {
                // maybe change this to run event handler instead
                throw new RuntimeException("Didn't get boolean response from client request");
            }
            boolean result = ((BooleanResultMessage) response).result;
            if (result) {
                hasPartner = true;
            }
            return result;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void waitForPartner() {
        try {
            ConnectionRequestMessage newConnection = new ConnectionRequestMessage(getName(), "", true);
            sendMessage(newConnection);
            synchronized (inputStream) {
                // wait for an message to come in
                inputStream.wait();
                // the response gets stored in connectionInfo
//                System.out.println(connectionInfo);
                hasPartner = true;
            }
        } catch (IOException ex) {
            close();
        } catch (InterruptedException ex) {
            System.err.println("Wait for partner interrupted");
        }
    }

    /**
     * Send a message than wait for a response that is of type wantedType
     * @param message The message to send
     * @return A message of the type asked for
     * @throws IOException If theres an exception in sending the message
     */
    // then wait for a response that matches the wantedType
    ResultMessage sendAndWait(Message message) throws IOException {
        sendMessage(message);
        // uses blocking queue
        // after adding blocking queue, I added a notify() call on inputStream in loop(), so you can use that instead
        // see waitForPartner
        // but for now, I'll use this still
        return resultQueue.poll();
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
        sendMessage(new StringMessage(MessageType.CHAT, message));
    }

    void sendMessage(Message message) throws IOException {
        System.out.println("Writing message");
        synchronized (outputStream) {
            outputStream.writeObject(message);
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
