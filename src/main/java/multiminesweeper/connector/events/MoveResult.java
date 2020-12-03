package multiminesweeper.connector.events;

public enum MoveResult {
    HIT("Hit a bomb :("),
    MISS("Hit nothing :)"),
    INVALID("Move position invalid :/"),
    PREVIOUS("Already did a move here!"), // previous move, already ran
    FLAG("Placed a flag"); // placed a flag (unknown result

    private final String message;

    MoveResult(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
