package multiminesweeper.connector.events;

import java.util.ArrayList;
import java.util.HashMap;

class MultiplayerEventDispatcher {
    private HashMap<MultiplayerEventType, ArrayList<MultiplayerEventListener>> eventListeners = new HashMap<>();

    public MultiplayerEventDispatcher() {
        // populate hashmap
        for (MultiplayerEventType type : MultiplayerEventType.values()) {
            eventListeners.put(type, new ArrayList<>());
        }
    }

    public void addEventListener(MultiplayerEventType type, MultiplayerEventListener listener) {
        eventListeners.get(type).add(listener);
    }

    public boolean removeEventListener(MultiplayerEventType type, MultiplayerEventListener listener) {
        return eventListeners.get(type).remove(listener);
    }

    public void triggerEvent(MultiplayerEventType type, String data) {
        var listeners = eventListeners.get(type);
        for (var listener : listeners) {
            listener.onEvent(data);
        }
    }
}
