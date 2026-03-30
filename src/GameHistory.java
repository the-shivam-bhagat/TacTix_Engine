import java.util.ArrayList;
import java.util.List;

/// Accumulates all completed sessions and prints a final summary.
final class GameHistory {

    private final List<GameSession> sessions = new ArrayList<>();

    /// Injected registry — used to trim and persist players at session end
    private final PlayerRegistry playerRegistry;

    GameHistory(PlayerRegistry registry) {
        this.playerRegistry = registry;
    }

    /// Add a completed session to the history
    void add(GameSession session) {
        sessions.add(session);
    }

    void print(InputHandler input) {
        System.out.println(Strings.GAME_SUMMARY_BOARD);

        if (sessions.isEmpty())
            System.out.println("📋 No games recorded yet.\n");
        else
            for (int i = 0; i < sessions.size(); i++)
                System.out.println(sessions.get(i).summaryLine(i + 1));

        // Trim registry to max size and persist, then show updated leaderboard
        playerRegistry.trimToMaxPlayers();
        System.out.println("\n🏆 Updated Global Leaderboard:\n");
        GameEngine.displayLeaderboard();

        System.out.print("🎮 Session complete. Thank you for playing! (Press ENTER to exit) ");
        input.waitForEnter();
    }
}