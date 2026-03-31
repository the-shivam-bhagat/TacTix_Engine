package player;

import java.util.List;

/// Read-only view of player rankings.
public interface RankingView {

    /// Top N players (usually top 10)
    List<Player> getTopPlayers(int limit);

    /// All players in ranked order
    List<Player> getAllPlayers();

    /// Total number of players
    int size();

    /// True if empty
    boolean isEmpty();
}