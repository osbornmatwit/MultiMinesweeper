package multiminesweeper.ui;

import multiminesweeper.Position;

import static multiminesweeper.ui.MineState.*;

public class MineEvent {
    public final MineState state;
    public final int bombNeighbors;
    public final Position position;
    public final boolean isBomb;

    public MineEvent(Position pos, boolean isBomb, int bombNeighbors) {
        this.isBomb = isBomb;
        this.bombNeighbors = bombNeighbors;
        position = pos;

        if (isBomb) {
            state = MINE;
        } else if (bombNeighbors > 0) {
            state = NUMBER;
        } else {
            state = BLANK;
        }
    }
}
