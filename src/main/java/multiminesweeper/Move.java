package multiminesweeper;

public class Move {
    public final int x;
    public final int y;
    public final boolean flag;

    public Move(int x, int y, boolean flag) {
        this.x = x;
        this.y = y;
        this.flag = flag;
    }

    public Move(int x, int y) {
        this(x, y, false);
    }

    @Override
    public String toString() {
        // \uD83D\uDEA9 =  🚩
        return String.format("(%d, %d)%s", x, y, flag ? "[\uD83D\uDEA9]" : "");
    }
}
