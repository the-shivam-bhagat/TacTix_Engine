package exception;

public class GameException extends RuntimeException {
    private final GameErrorCode errorCode;

    public GameException(GameErrorCode errorCode, String message) {
        super(String.format("[%s] %s", errorCode.name(), message));
        this.errorCode = errorCode;
    }

    public GameException(GameErrorCode errorCode, String message, Throwable cause) {
        super(String.format("[%s] %s", errorCode.name(), message), cause);
        this.errorCode = errorCode;
    }

    public GameErrorCode getErrorCode() {
        return errorCode;
    }
}
