package exception;

import sessions.SessionType;

/**
 * Thrown when an unrecognized or unsupported game session type is requested.
 */
public class InvalidSessionException extends GameException {

    private final Integer requestedValue;   // raw input (e.g. 4)
    private final SessionType requestedType; // resolved type (if available)

    // Constructor for invalid raw input (most common case)
    public InvalidSessionException(int value) {
        super(
                GameErrorCode.INVALID_SESSION_TYPE,
                "Invalid session type selected: " + value
                        + ". Valid options are: 1 (PVP), 2 (PVB), 3 (BVB)."
        );
        this.requestedValue = value;
        this.requestedType = null;
    }

    // Constructor for already parsed enum (optional usage)
    public InvalidSessionException(SessionType type) {
        super(
                GameErrorCode.INVALID_SESSION_TYPE,
                "No session exists for type: " + type
        );
        this.requestedType = type;
        this.requestedValue = null;
    }

    @SuppressWarnings("unused")
    public Integer getRequestedValue() {
        return requestedValue;
    }

    @SuppressWarnings("unused")
    public SessionType getRequestedType() {
        return requestedType;
    }
}