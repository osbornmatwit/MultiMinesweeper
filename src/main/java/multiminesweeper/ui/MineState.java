package multiminesweeper.ui;

public enum MineState {
    BLANK,
    NUMBER,
    MINE;

    public int mineCount = 0;

    MineState() {
    }

    MineState(int mineCount) {
        this.mineCount = mineCount;
    }
}
