package exception;

/**
 * Centralized error codes for all game exceptions.
 * Enables programmatic handling, logging, and filtering by error type.
 */
public enum GameErrorCode {
    // Session errors
    INVALID_SESSION_TYPE,
    SESSION_ALREADY_ACTIVE,
    SESSION_NOT_INITIALIZED,

    // Bot errors
    INVALID_BOT_LEVEL,
    BOT_INSTANTIATION_FAILED,

    // Move / input errors
    INVALID_MOVE,
    INVALID_INPUT,
    POSITION_ALREADY_OCCUPIED,

    // Game state errors
    GAME_NOT_STARTED,
    GAME_ALREADY_OVER,
    INVALID_BOARD_STATE,

    // System / IO errors
    STORAGE_LOAD_FAILED,
    STORAGE_SAVE_FAILED
}