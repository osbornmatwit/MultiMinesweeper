package multiminesweeper.message;

import java.net.SocketAddress;

public class ConnectionMessage extends Message {
    // the information the connector needs to connect to the new partner
    // null if the connector doesn't support it
    public final SocketAddress partnerAddress;

    // a name to show the user who they are connecting to
    public final String name;

    // Used in peer to peer, to determine who is the host
    public final boolean host;

    public ConnectionMessage(SocketAddress partnerAddress, String name, boolean host) {
        super(MessageType.INIT_CONNECTION);
        this.partnerAddress = partnerAddress;
        this.name = name;
        this.host = host;
    }

    public ConnectionMessage(SocketAddress partnerAddress, String name) {
        this(partnerAddress, name, false);
    }

    public ConnectionMessage(String name) {
        this(null, name, false);
    }

    @Override
    public String toString() {
        if (partnerAddress == null) {
            return String.format("Connected to %s through relay", name);
        } else {
            return String.format("Connected to %s at %s", name, partnerAddress);
        }
    }
}
