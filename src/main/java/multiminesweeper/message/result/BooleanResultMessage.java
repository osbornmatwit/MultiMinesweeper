package multiminesweeper.message.result;

public class BooleanResultMessage extends ResultMessage {
    // what this result is from i.e. connections available
    public boolean result;

    public BooleanResultMessage(String resultLabel, boolean result) {
        super(resultLabel);
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", resultLabel, result);
    }
}
