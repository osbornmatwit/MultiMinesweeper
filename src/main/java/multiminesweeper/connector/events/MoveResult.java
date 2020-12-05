package multiminesweeper.connector.events;

// TODO: Refactor MoveResult to be a class with the squares to reveal.
public enum MoveResult {
    HIT("Hit a bomb :("),
    MISS("Hit nothing :)"),
    INVALID("Move position invalid :/"),
    PREVIOUS("Already did a move here!"), // previous move, already ran
    FLAG("Placed a flag"),
    NO_HANDLER("No handler set to handle moves"); // placed a flag (unknown result

    private final String message;

    MoveResult(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
