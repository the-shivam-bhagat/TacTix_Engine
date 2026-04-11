package exception;

public class SessionEndException extends RuntimeException {
    public SessionEndException() {
        super("Session terminated by user command");
    }
}
