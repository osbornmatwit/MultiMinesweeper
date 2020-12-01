package multiminesweeper.connector.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class MultiplayerEventDispatcher {
    private final HashMap<EventType, ArrayList<Consumer<MultiplayerEvent>>> eventListeners = new HashMap<>();

    public MultiplayerEventDispatcher() {
        // populate hashmap
        for (EventType type : EventType.values()) {
            eventListeners.put(type, new ArrayList<>());
        }
    }

    public void addEventListener(EventType type, Consumer<MultiplayerEvent> listener) {
        eventListeners.get(type).add(listener);
    }

    public boolean removeEventListener(EventType type, Consumer<MultiplayerEvent> listener) {
        return eventListeners.get(type).remove(listener);
    }

    public void triggerEvent(MultiplayerEvent event) {
        var listeners = eventListeners.get(event.type);
        for (var listener : listeners) {
            listener.accept(event);
        }
    }
}
