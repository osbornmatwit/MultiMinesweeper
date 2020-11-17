package multiminesweeper.connector.events;

import java.util.EventListener;

public interface MultiplayerEventListener extends EventListener {
    void onEvent(String data);
}
