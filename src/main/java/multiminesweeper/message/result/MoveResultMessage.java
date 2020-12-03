package multiminesweeper.message.result;

import multiminesweeper.Move;
import multiminesweeper.connector.events.MoveResult;

public class MoveResultMessage extends ResultMessage {
    public final Move move;
    public final MoveResult result;

    public MoveResultMessage(Move move, MoveResult result) {
        super(String.format("Move %s result: %s", move, result));
        this.move = move;
        this.result = result;
    }
}
