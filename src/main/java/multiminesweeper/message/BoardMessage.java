package multiminesweeper.message;

import multiminesweeper.ui.Minefield;

public class BoardMessage extends Message {
    public final Minefield state;

    public BoardMessage(Minefield state) {
        super(MessageType.BOARD);
        this.state = state;
    }
}
