package multiminesweeper.message;

public class DisconnectMessage extends Message {
    public final String reason;

    public DisconnectMessage(String reason) {
        super(MessageType.DISCONNECT);
        if (reason == null) {
            reason = "Unknown reason";
        }
        this.reason = reason;
    }

    @Override
    public String toString() {
        return reason;
    }
}
