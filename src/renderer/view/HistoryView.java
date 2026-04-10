package renderer.view;

import core.GameResult;

import java.util.List;

public interface HistoryView {
    void showSessions(List<GameResult> sessions);
}
