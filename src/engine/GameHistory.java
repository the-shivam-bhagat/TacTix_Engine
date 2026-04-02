package engine;

import renderer.view.HistoryView;

import java.util.ArrayList;
import java.util.List;

public final class GameHistory {

    private final List<GameResult> sessions = new ArrayList<>();
    private final HistoryView renderer;

    public GameHistory(HistoryView renderer) {
        this.renderer = renderer;
    }

    public void add(PvPGameSession session) {
        sessions.add(session.toResult());
    }

    public void showHistory() {
        renderer.showSessions(sessions);
    }
}