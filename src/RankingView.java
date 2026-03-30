import java.util.Iterator;

/// Read-only view of player rankings.
public interface RankingView {

    /// Iterate players in rank order (highest wins first)
    Iterator<Player> iterator();

    /// Peek at the top-ranked player without removing
    Player peekTopPlayer();

    /// Total number of registered players
    int size();

    /// True if no players are registered
    boolean isEmpty();
}