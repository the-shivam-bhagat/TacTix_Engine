package command;

public interface CommandProcessor {
    boolean handle(String line);
}