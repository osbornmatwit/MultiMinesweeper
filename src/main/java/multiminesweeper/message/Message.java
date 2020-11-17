package multiminesweeper.message;

public class Message {
    public final String message;
    public final String[] parts;
    public final MessageType type;

    public Message(String message) {
        this.message = message;
        type = MessageType.fromString(message.split(":", 2)[0]);
        parts = message.split(":", type.partCount);
    }

}
