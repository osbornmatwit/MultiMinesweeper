package multiminesweeper.connector;

public abstract class AbstractConnector {
    /**
     * Try and find a partner, return true if found, false if not
     * @return if a partner is found, true, false otherwise
     */
    public abstract boolean tryFindPartner();

    /**
     * Block until a partner is available
     */
    public abstract void waitForPartner();

    public abstract boolean hasPartner();

    /**
     * Checks if the connection was previously open
     * @return True if the connection was closed for some reason, false if it hasn't been opened, or if it hasn't been closed
     */
    public abstract boolean hasClosed();
}
