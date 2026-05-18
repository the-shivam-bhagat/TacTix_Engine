package sessions;

import core.GameResult;

public interface Game {

    void play();

    GameResult toResult();

    SessionType getSessionType();
}