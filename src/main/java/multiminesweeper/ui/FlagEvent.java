package multiminesweeper.ui;

import multiminesweeper.Position;

public class FlagEvent {
    public final boolean newValue;
    public final Position position;

    public FlagEvent(Position position, boolean newValue, boolean oldValue) {
        this.newValue = newValue;
        this.position = position;
    }
}
