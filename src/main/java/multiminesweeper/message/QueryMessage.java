package multiminesweeper.message;

// expects a response
// asks a question about the peer
public class QueryMessage extends Message {
    public final String queryString;

    public QueryMessage(String request) {
        super(MessageType.INFO_QUERY);
        this.queryString = request;
    }
}
