package multiminesweeper.message;

public class ErrorMessage extends Message {
    public final String error;

    public ErrorMessage(String error) {
        super(MessageType.ERROR);
        this.error = error;
    }

    @Override
    public String toString() {
        return error;
    }
}
