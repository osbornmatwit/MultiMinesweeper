package multiminesweeper.message;

import java.io.Serializable;

public class Message implements Serializable {
    public final MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
