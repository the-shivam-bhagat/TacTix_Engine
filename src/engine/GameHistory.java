package engine;

import renderer.HistoryRenderer;

import java.util.ArrayList;
import java.util.List;

public final class GameHistory {

    private final List<GameResult> sessions = new ArrayList<>();
    private final HistoryRenderer renderer;

    public GameHistory(HistoryRenderer renderer) {
        this.renderer = renderer;
    }

    public void add(GameSession session) {
        sessions.add(session.toResult());
    }

    public void showHistory() {
        renderer.showSessions(sessions);
    }
}