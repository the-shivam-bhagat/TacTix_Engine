package engine;

public interface GameSession {

    void play();

    GameResult toResult();

    String getSessionType();
}