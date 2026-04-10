package exception;

/**
 * Thrown when an invalid bot difficulty level is requested.
 * Contains the invalid level and the session context it was requested from.
 */
public class InvalidBotSelectionException extends GameException {

    private final int requestedLevel;

    public InvalidBotSelectionException(int requestedLevel) {
        super(
                GameErrorCode.INVALID_BOT_LEVEL,
                "No bot exists for difficulty level: " + requestedLevel
        );
        this.requestedLevel = requestedLevel;
    }

    public int getRequestedLevel() {
        return requestedLevel;
    }
}