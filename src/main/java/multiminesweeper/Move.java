package multiminesweeper;

import java.io.Serializable;

public class Move implements Serializable {
    public final int x;
    public final int y;
    public final boolean flag;
    public final boolean newValue;

    public Move(int x, int y, boolean flag, boolean newValue) {
        this.x = x;
        this.y = y;
        this.flag = flag;
        this.newValue = newValue;
    }

    public Move(int x, int y) {
        this(x, y, false, false);
    }

    @Override
    public String toString() {
        // \uD83D\uDEA9 =  🚩
        return String.format("(%d, %d)%s", x, y, flag ? "[\uD83D\uDEA9]" : "");
    }
}
