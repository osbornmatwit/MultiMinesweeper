package multiminesweeper.connector;

import multiminesweeper.connector.events.EventType;
import multiminesweeper.connector.events.MultiplayerEvent;
import multiminesweeper.connector.events.MultiplayerEventDispatcher;
import multiminesweeper.message.Message;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class AbstractConnector {
    final MultiplayerEventDispatcher dispatcher = new MultiplayerEventDispatcher();

    /**
     * Try and find a partner, return true if found, false if not
     * @return if a partner is found, true, false otherwise
     */
    public abstract boolean tryFindPartner();

    /**
     * Block until a partner is available
     */
    public abstract void waitForPartner() throws IOException;

    /**
     * Check if a partner is connected on this connector
     * @return True if there is a partner, false otherwise
     */
    public abstract boolean hasPartner();

    public abstract void sendChat(String message) throws IOException;

    /**
     * Checks if the connection was previously open
     * @return True if the connection was closed for some reason, false if it hasn't been opened, or if it hasn't been closed
     */
    public abstract boolean hasClosed();

    public void addEventListener(EventType type, Consumer<MultiplayerEvent> listener) {
        dispatcher.addEventListener(type, listener);
    }

    public boolean removeEventListener(EventType type, Consumer<MultiplayerEvent> listener) {
        return dispatcher.removeEventListener(type, listener);
    }

    void sendEvent(Message message) {
        dispatcher.triggerEvent(new MultiplayerEvent(message));
    }

}
