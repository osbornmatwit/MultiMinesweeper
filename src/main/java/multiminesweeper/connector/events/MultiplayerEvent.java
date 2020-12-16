package multiminesweeper.connector.events;

import multiminesweeper.Move;
import multiminesweeper.Position;
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
            case BOARD:
                data = "Use the original message";
                break;
            case GAME_OVER:
                data = "";
                break;
            case MOVE:
                Move move = ((MoveMessage) message).move;
                String result = "";

                if (move.flag) {
                    if (move.newValue) {
                        result = "set flag on ";
                    } else {
                        result = "removed flag from ";
                    }
                } else {
                    result = "activated ";
                }
                data = result + new Position(move.x, move.y).toString();
                break;
            case READY:
                data = "";
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
            case GAME_OVER:
                return EventType.GAME_OVER;
            case BOARD:
                return EventType.BOARD;
            case READY:
                return EventType.READY;
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
