package multiminesweeper.connector.events;

import multiminesweeper.message.*;

public class MultiplayerEvent {
    public final EventType type;
    public final Message originalMessage;
    public final String data;

    // turn server sent events that are useful for game logic into
    public MultiplayerEvent(Message message) {
        originalMessage = message;
        type = extractType(message);
        if (type == null) {
            data = null;
            return;
        }
        switch (type) {
            case CONNECT:
                data = ((ConnectionMessage) message).name;
                break;
            case ERROR:
                data = ((ErrorMessage) message).error;
                break;
            case DISCONNECT:
                data = ((DisconnectMessage) message).reason;
                break;
            case CHAT:
                data = ((StringMessage) message).message;
                break;
            case MOVE:
                MoveMessage move = ((MoveMessage) message);
                // have to send it this way because can't have sum types easily :(
                data = String.format("(%d, %d)", move.x, move.y);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    private EventType extractType(Message message) {
        switch (message.type) {
            case ERROR:
                return EventType.ERROR;
            case INIT_CONNECTION:
                return EventType.CONNECT;
            case DISCONNECT:
                return EventType.DISCONNECT;
            case CHAT:
                return EventType.CHAT;
            case MOVE:
                return EventType.MOVE;
            default:
                return null;
        }
    }

    public MultiplayerEvent(EventType type, String data) {
        this.type = type;
        this.data = data;
        this.originalMessage = null;
    }
}
