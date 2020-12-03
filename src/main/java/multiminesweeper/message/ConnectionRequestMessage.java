package multiminesweeper.message;

public class ConnectionRequestMessage extends Message {
    // if not empty, must match with another client
    public final String password;
    public final String name;
    public final boolean blocking;

    public ConnectionRequestMessage(String name, String password, boolean blocking) {
        super(MessageType.CONNECTION_REQUEST);
        this.name = name;
        this.password = password;
        this.blocking = blocking;
    }

    // If you don't care about settings
    public ConnectionRequestMessage(String name) {
        this(name, "", false);
    }
}
