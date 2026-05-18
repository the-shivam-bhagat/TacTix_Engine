package core;

import replay.ReplayEngine;
import renderer.view.HistoryView;
import sessions.Game;

import java.util.ArrayList;
import java.util.List;

public final class GameHistory {

    private final List<GameResult> sessions = new ArrayList<>();
    private final HistoryView renderer;
    private final ReplayEngine replayEngine;

    public GameHistory(HistoryView renderer, ReplayEngine replayEngine) {
        this.renderer      = renderer;
        this.replayEngine  = replayEngine;
    }

    public void add(Game session) {
        sessions.add(session.toResult());
    }

    /// Shows history table then offers replay
    public void showHistory() {
        renderer.showSessions(sessions);
        replayEngine.offerReplay(sessions);
    }
}