package multiminesweeper.ui;

public enum MineState {
    BLANK,
    FLAG,
    NUMBER,
    MINE;

    public int mineCount = 0;

    MineState() {
    }

    MineState(int mineCount) {
        this.mineCount = mineCount;
    }
}
