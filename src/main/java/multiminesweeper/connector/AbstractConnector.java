package multiminesweeper.connector;

import multiminesweeper.Move;
import multiminesweeper.connector.events.EventDispatcher;
import multiminesweeper.connector.events.EventType;
import multiminesweeper.connector.events.MoveResult;
import multiminesweeper.connector.events.MultiplayerEvent;
import multiminesweeper.message.*;
import multiminesweeper.message.result.MoveResultMessage;
import multiminesweeper.message.result.ResultMessage;
import multiminesweeper.ui.Minefield;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractConnector {
    final EventDispatcher dispatcher = new EventDispatcher();
    private String name = "";
    public boolean debug = false;
    private Runnable onClose;

    AbstractConnector(String name) {
        this.name = name;
    }

    public AbstractConnector() {

    }

    public void debugPrint(String value) {
        if (debug) {
            System.err.print(value);
        }
    }

    public void debugPrintln(String value) {
        if (debug) {
            System.err.println(value);
        }
    }

    // tries to change the name on both this end and on the other end
    public boolean setName(String newName) {
        if (!hasPartner()) {
            name = newName;
            return true;
        }

        sendMessage(new InfoChangeMessage("name", newName));
        name = newName;
        return true;
    }

    public String getName() {
        return name;
    }

    public abstract String getPartnerName();

    /**
     * Try and find a partner, return true if found, false if not
     * @return if a partner is found, true, false otherwise
     */
    public abstract boolean tryFindPartner();

    /**
     * Block until a partner is available
     */
    public abstract void waitForPartner(String password);

    public void waitForPartner() {
        waitForPartner("");
    }

    /**
     * Check if a partner is connected on this connector
     * @return True if there is a partner, false otherwise
     */
    public abstract boolean hasPartner();

    public abstract void sendChat(String message);


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

    public void setMoveHandler(Function<Move, MoveResult> handler) {
        dispatcher.setMoveHandler(handler);
    }

    public boolean hasMoveHandler() {
        return dispatcher.hasMoveHandler();
    }

    void sendEvent(Message message) {
        dispatcher.triggerEvent(new MultiplayerEvent(message));
    }

    public abstract void sendMessage(Message message);

    abstract ResultMessage sendAndWait(Message message);

    public MoveResult sendMove(Move move) {
        return ((MoveResultMessage) sendAndWait(new MoveMessage(move))).result;
    }

    public void sendBoard(Minefield field) {
        sendMessage(new BoardMessage(field));
    }

    public void gameOver() {
        sendMessage(new GameOverMessage());
    }

    public void close() {
        if (this.onClose != null) onClose.run();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public Runnable getOnClose() {
        return onClose;
    }
}
