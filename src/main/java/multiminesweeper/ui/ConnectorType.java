package multiminesweeper.ui;

public enum ConnectorType {
    RELAY("Relay server"),
    P2P("Peer to Peer"),
    LOCAL_P2P("Full Peer to Peer");

    private final String niceName;

    ConnectorType(String niceName) {
        this.niceName = niceName;
    }

    @Override
    public String toString() {
        return niceName;
    }
}
