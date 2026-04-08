package player;

/// Full mutable registry contract.
/// GameEngine, PvsPGameSession, and InputHandler depend on this —
/// never on the concrete PlayerRegistry class directly.
public interface Registry {

    /// Add a new player (if not already present)
    void addPlayer(Player player);

    /// If player exists returns it, if not then create and return
    PlayerResult getOrCreatePlayer(String name);

    /// Delete player by name
    boolean deletePlayerByName(String name);

    /// Safely update win count
    void incrementWin(Player player);

    /// Keep registry within max size, then persist
    void trimToMaxPlayers();
}