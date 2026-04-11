package exception;

public class UndoRequestException extends RuntimeException {
    public UndoRequestException() {
        super("Undo requested by user command");
    }
}