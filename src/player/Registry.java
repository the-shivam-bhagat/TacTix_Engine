package player;

/// Full mutable registry contract.
/// GameEngine, PlayerVSPlayerSession, and InputHandler depend on this —
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

    /// Auth-related - related
    @SuppressWarnings("UnusedReturnValue")
    boolean setPassword(String name, String rawPassword);
    boolean verifyPassword(String name, String rawPassword);

    void setLifetimeWins(String name, int wins, String AdminPassword);

    void removePassword(String name, String rawPassword);

    boolean renamePlayer(String name, String newName, String AdminPassword);
}