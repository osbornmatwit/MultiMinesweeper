package multiminesweeper.connector.events;

import multiminesweeper.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Sends events to listeners
 */
public class EventDispatcher {
    private final HashMap<EventType, ArrayList<Consumer<MultiplayerEvent>>> eventListeners = new HashMap<>();
    private final ArrayList<Consumer<MultiplayerEvent>> globalEventListeners = new ArrayList<>();
    private Function<Move, MoveResult> moveHandler;

    public EventDispatcher() {
        // populate hashmap
        for (EventType type : EventType.values()) {
            eventListeners.put(type, new ArrayList<>());
        }
    }

    /**
     * Adds an event listener that will only run on events of the specific type.
     * @param type The type of events to listen for.
     * @param listener The listener
     */
    public void addEventListener(EventType type, Consumer<MultiplayerEvent> listener) {
        eventListeners.get(type).add(listener);
    }

    /**
     * Remove a specific listener from listening to a type of event.
     * @param type The type to remove the listener from.
     * @param listener T
     * @return {@code true} if the listener was in the list and removed.
     */
    public boolean removeEventListener(EventType type, Consumer<MultiplayerEvent> listener) {
        return eventListeners.get(type).remove(listener);
    }

    public void addGlobalEventListener(Consumer<MultiplayerEvent> listener) {
        globalEventListeners.add(listener);
    }

    public void removeGlobalEventListener(Consumer<MultiplayerEvent> listener) {
        globalEventListeners.remove(listener);
    }

    /**
     * Sends an event to any attached listeners based on the type.
     * @param event The event to send
     */
    public void triggerEvent(MultiplayerEvent event) {
        var listeners = globalEventListeners;
        for (var listener : listeners) {
            listener.accept(event);
        }

        listeners = eventListeners.get(event.type);
        for (var listener : listeners) {
            listener.accept(event);
        }
    }

    /**
     * Set a handler for move events from the multiplayer partner that displays the moves locally
     * and returns the result of the move on the game board.
     * @param handler The function that checks and applies the move
     */
    public void setMoveHandler(Function<Move, MoveResult> handler) {
        moveHandler = handler;
    }

    /**
     * Checks if there is a moveHandler defined.
     * @return true if there is a handler, false otherwise
     */
    public boolean hasMoveHandler() {
        return moveHandler != null;
    }

    /**
     * Run a move against the set handler
     * @param move The move to run
     * @return The result of the move
     */
    public MoveResult runMove(Move move) {
        if (!hasMoveHandler()) throw new IllegalStateException("No move handler defined!");
        return moveHandler.apply(move);
    }
}
