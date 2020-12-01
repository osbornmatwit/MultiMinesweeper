package multiminesweeper.message;

public class StringMessage extends Message {
    public final String message;

    public StringMessage(MessageType type, String message) {
        super(type);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
