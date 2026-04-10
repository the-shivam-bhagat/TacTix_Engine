package sessions;

import core.GameResult;
import core.SessionType;

public interface GameSession {

    void play();

    GameResult toResult();

    SessionType getSessionType();
}