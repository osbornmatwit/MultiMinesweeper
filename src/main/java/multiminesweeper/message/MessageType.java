package multiminesweeper.message;

public enum MessageType {
    SYSTEM("system", 2),
    CHAT("chat", 2),
    RESULT("result", 2),
    INFO("info", 3);

    private final String name;
    public final int partCount;

    MessageType(String name, int partCount) {
        this.name = name;
        this.partCount = partCount;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MessageType fromString(String typeString) {
        switch (typeString.toLowerCase()) {
            case "system":
                return SYSTEM;
            case "chat":
                return CHAT;
            case "result":
                return RESULT;
            case "info":
                return INFO;
            default:
                throw new IllegalArgumentException("No MessageType for " + typeString);
        }
    }


}
