package multiminesweeper.message.result;


public class StringResultMessage extends ResultMessage {
    public final String result;

    public StringResultMessage(String resultLabel, String result) {
        super(resultLabel);
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", resultLabel, result);
    }
}
