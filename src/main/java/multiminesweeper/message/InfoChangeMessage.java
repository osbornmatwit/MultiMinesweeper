package multiminesweeper.message;

// changing metadata about you on the other end
public class InfoChangeMessage extends Message {
    public final String property;
    public final String value;

    public InfoChangeMessage(String property, String value) {
        super(MessageType.CHANGE_INFO);
        this.property = property;
        this.value = value;
    }
}
