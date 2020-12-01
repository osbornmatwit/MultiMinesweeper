package multiminesweeper.message.result;

import multiminesweeper.message.Message;
import multiminesweeper.message.MessageType;

public abstract class ResultMessage extends Message {
    public final String resultLabel;

    public ResultMessage(String resultLabel) {
        super(MessageType.RESULT);
        this.resultLabel = resultLabel;
    }
}
