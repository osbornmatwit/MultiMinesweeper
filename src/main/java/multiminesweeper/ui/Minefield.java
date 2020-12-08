package multiminesweeper.ui;

import multiminesweeper.Position;

import java.io.Serializable;

public class Minefield implements Serializable {
    public Position[] mines;
    public Position startingPosition;
    int width;
    int height;

    // Constructors
    public Minefield(int width, int height, Position[] mines, Position startingPosition) {
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.startingPosition = startingPosition;
    }

    public Minefield(GameState state) {
        width = state.width;
        height = state.height;
        startingPosition = state.startingLocation;
        mines = state.getBombList();
    }
}