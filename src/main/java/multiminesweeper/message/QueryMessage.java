package multiminesweeper.message;

// expects a response
// asks a question about the peer
public class QueryMessage extends Message {
    public final String property;

    public QueryMessage(String property) {
        super(MessageType.INFO_QUERY);
        this.property = property;
    }
}
