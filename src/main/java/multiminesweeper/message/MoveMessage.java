package multiminesweeper.message;

import multiminesweeper.Move;

public class MoveMessage extends Message {
    public final Move move;

    public MoveMessage(Move move) {
        super(MessageType.MOVE);
        this.move = move;
    }
}
