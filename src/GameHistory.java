import java.util.ArrayList;
import java.util.List;

final class GameHistory {

    private final List<GameSession> sessions = new ArrayList<>();
    private final HistoryRenderer renderer;

    GameHistory(HistoryRenderer renderer) {
        this.renderer = renderer;
    }

    void add(GameSession session) {
        sessions.add(session);
    }

    void showHistory() {
        renderer.showSummaryHeader();
        renderer.showSessions(sessions);
    }
}