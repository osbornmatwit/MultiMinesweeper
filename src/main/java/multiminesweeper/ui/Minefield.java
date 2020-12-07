package multiminesweeper.ui;

import multiminesweeper.Position;

public class Minefield {
    public Position[] mines;
    public Position startingPosition;

    // Constructors
    public Minefield(Position[] mines, Position startingPosition) {
        this.mines = mines;
        this.startingPosition = startingPosition;
    }
}