package multiminesweeper.message;

public class MoveMessage extends Message {
    // TODO: Use some kind of Position class and maybe a separate system for position requests from partner (handler)
    public final int x;
    public final int y;

    public MoveMessage(int x, int y) {
        super(MessageType.MOVE);
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
