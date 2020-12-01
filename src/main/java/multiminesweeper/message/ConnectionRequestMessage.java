package multiminesweeper.message;

public class ConnectionRequestMessage extends Message {
    // if not empty, must match with another client
    public final String password;
    public final boolean blocking;

    public ConnectionRequestMessage(String password, boolean blocking) {
        super(MessageType.CONNECTION_REQUEST);
        this.password = password;
        this.blocking = blocking;
    }

    public ConnectionRequestMessage(boolean blocking) {
        this("", blocking);
    }

    public ConnectionRequestMessage(String password) {
        this(password, false);
    }

    public ConnectionRequestMessage() {
        this("", false);
    }
}
