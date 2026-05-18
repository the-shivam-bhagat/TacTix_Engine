package sessions;

import input.Input;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GameSession implements Game {
    protected final Input input;
    protected final SessionView renderer;
    protected final PlayBoardView playBoardView;

    protected final SessionType sessionType;

    public GameSession(Input input, SessionView renderer, PlayBoardView playBoardView, SessionType sessionType) {
        this.input = input;
        this.renderer = renderer;
        this.playBoardView = playBoardView;

        this.sessionType = sessionType;
    }

    // Per-round parallel lists — all same length, same index = same round
    protected final List<List<Integer>> allRoundMoves = new ArrayList<>();
    protected final List<String> allRoundFirstPlayerStarts = new ArrayList<>();
    protected final List<String> allRoundWinners = new ArrayList<>();


    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    // ---------- Safe Read-only Accessors ----------

    public final List<List<Integer>> getAllRoundMoves() {
        return Collections.unmodifiableList(allRoundMoves);
    }

    public final List<String> getAllRoundFirstPlayerStarts() {
        return Collections.unmodifiableList(allRoundFirstPlayerStarts);
    }

    public final List<String> getAllRoundWinners() {
        return Collections.unmodifiableList(allRoundWinners);
    }
}
