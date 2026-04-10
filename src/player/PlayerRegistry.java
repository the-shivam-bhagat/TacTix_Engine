package player;

import exception.GameErrorCode;
import exception.GameException;
import player.store.PlayerStore;
import utility.Config;
import utility.Logger;

import java.io.IOException;
import java.util.*;

/// Implements Registry (mutable operations) and RankingView (read-only operations).
/// Callers that only display rankings depend on RankingView.
/// Callers that manage players depend on Registry.
/// Neither needs to know about PlayerRegistry directly.
public final class PlayerRegistry implements Registry, RankingView {
    private static final int AUTO_NAME_LIMIT = Config.PlayerConfig.AUTO_NAME_LIMIT;

    /// Fast lookup by player name
    private final Map<String, Player> players;

    /// Sorted ranking (highest wins first)
    private final TreeSet<Player> ranking;

    /// player storage
    private final PlayerStore store;

    public static final int TOP_PLAYERS = Config.PlayerConfig.TOP_PLAYERS;
    private static final int MAX_PLAYERS = Config.PlayerConfig.MAX_PLAYERS;

    /// IOException from store.loadAll() is caught here and wrapped as GameException
    /// so no checked exception leaks to GameEngine.initialize()
    public PlayerRegistry(PlayerStore store) {
        this.store = store;
        players = new HashMap<>();
        ranking = new TreeSet<>();

        Logger.info("Loading players from storage");
        try {
            for (Player p : store.loadAll()) {
                players.put(p.getName(), p);
                ranking.add(p);
            }
        } catch (IOException e) {
            Logger.error("Failed to load player data from storage", e);
            throw new GameException(
                    GameErrorCode.STORAGE_LOAD_FAILED,
                    "Could not load player data: " + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // Registry implementation (mutable operations)
    // =====================================================

    /// Add a new player (if not already present)
    @Override
    public void addPlayer(Player player) {
        if (players.containsKey(player.getName())) return;

        players.put(player.getName(), player);
        ranking.add(player);
        Logger.info("New player registered: " + player.getName());
    }

    /// if player exists returns it, if not then create and return
    @Override
    public PlayerResult getOrCreatePlayer(String name) {

        // Case 1: Existing player
        Player existing = players.get(name);
        if (existing != null) {
            Logger.info("Existing player loaded: " + name);
            return new PlayerResult(existing, false);
        }

        // Case 2: New player
        if (name == null || name.isEmpty()) {
            name = generateUnusedName();
        }

        Player newPlayer = new Player(name);
        addPlayer(newPlayer);

        return new PlayerResult(newPlayer, true);
    }

    /// Internal removal helper
    private void removePlayer(Player player) {
        players.remove(player.getName());
        ranking.remove(player);
        Logger.warn("Player deleted: " + player.getName());
    }

    /// Delete player by name
    @Override
    public boolean deletePlayerByName(String name) {
        Player player = players.get(name);
        if (player == null) return false;

        removePlayer(player);
        return true;
    }

    /// Safely update win count — remove first so TreeSet re-sorts correctly
    @Override
    public void incrementWin(Player player) {
        if (player == null) {
            Logger.warn("Attempted to increment win for null player");
            return;
        }

        ranking.remove(player);
        player.incrementLifetimeWins();
        Logger.info("Win recorded for player: " + player.getName());
        ranking.add(player);
    }

    /// Keep registry within max size, then persist via the injected store
    /// RuntimeException from store.saveAll() is wrapped as GameException(STORAGE_SAVE_FAILED)
    @Override
    public void trimToMaxPlayers() {

        while (ranking.size() > MAX_PLAYERS) {
            Player lowest = ranking.pollLast();
            if (lowest != null) players.remove(lowest.getName());
        }
        Logger.info("Trimming to max players");

        try {
            store.saveAll(ranking);
        } catch (RuntimeException e) {
            Logger.error("Failed to save player data to storage", e);
            throw new GameException(
                    GameErrorCode.STORAGE_SAVE_FAILED,
                    "Could not save player data: " + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // RankingView implementation (read-only)
    // =====================================================

    /// give top players for leaderboard
    @Override
    public List<Player> getTopPlayers(int limit) {
        List<Player> result = new ArrayList<>(limit);

        int count = 0;
        for (Player p : ranking) {
            if (count++ >= limit) break;
            result.add(p);
        }

        return result;
    }

    /// give list of all players
    @Override
    public List<Player> getAllPlayers() {
        return new ArrayList<>(ranking);
    }

    /// Total number of registered players
    @Override
    public int size() {
        return players.size();
    }

    /// True if no players are registered
    @Override
    public boolean isEmpty() {
        return players.isEmpty();
    }

    // =====================================================
    // Private helpers
    // =====================================================

    /// Auto-assigns the first unused PLAYER_N name (fallback: timestamp-based)
    private String generateUnusedName() {
        for (int i = 1; i <= AUTO_NAME_LIMIT; i++) {
            String newName = "PLAYER_" + i;
            if (!players.containsKey(newName)) return newName;
        }
        return "PLAYER_" + System.currentTimeMillis(); // fallback if all slots taken
    }
}