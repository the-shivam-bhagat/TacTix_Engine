package renderer.view;

import engine.GameResult;

import java.util.List;

public interface HistoryView {
    void showSessions(List<GameResult> sessions);
}
