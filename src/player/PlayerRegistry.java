package player;

import myUtil.Strings;

import java.io.IOException;
import java.util.*;

/// Implements Registry (mutable operations) and RankingView (read-only operations).
/// Callers that only display rankings depend on RankingView.
/// Callers that manage players depend on Registry.
/// Neither needs to know about PlayerRegistry directly.
public final class PlayerRegistry implements Registry, RankingView {

    /// Fast lookup by player name
    private final Map<String, Player> players;

    /// Sorted ranking (highest wins first)
    private final TreeSet<Player> ranking;

    /// player storage
    private final PlayerStore store;

    public static final int TOP_PLAYERS = 10;
    private static final int MAX_PLAYERS = 1000;

    /// We could have also used singleton but, it is too much for our project
    public PlayerRegistry(PlayerStore store) throws IOException {
        this.store   = store;
        players = new HashMap<>();
        ranking = new TreeSet<>();

        // loadAll() returns a plain List — registry owns its own internal structure
        for (Player p : store.loadAll()) {
            players.put(p.getName(), p);
            ranking.add(p);
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
    }

    // for greeting message
    private static boolean flip = (int) Math.floor(Math.random() * 100) % 2 == 0;

    /// if player exists returns it, if not then create and return
    @Override
    public Player getPlayer(String name) {
        // Case 1: Player exists
        Player existing = players.get(name);
        if (existing != null) {
            if (flip) System.out.printf(Strings.WELCOME_NEW_PLAYER_1, name, existing.getLifetimeWins());
            else System.out.printf(Strings.WELCOME_NEW_PLAYER_2, name, existing.getLifetimeWins());
            flip = !flip;
            return existing;
        }

        // Case 2: New player
        // no name — auto-assign the first unused PLAYER_N slot
        if (name == null || name.isEmpty()) {
            name = generateUnusedName();
        }

        Player newPlayer = new Player(name);
        addPlayer(newPlayer);

        if (flip) System.out.printf(Strings.WELCOME_REGISTERED_PLAYER_1, name);
        else System.out.printf(Strings.WELCOME_REGISTERED_PLAYER_2, name);

        flip = !flip;
        return newPlayer;
    }

    /// Internal removal helper
    private void removePlayer(Player player) {
        players.remove(player.getName());
        ranking.remove(player);
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
        if (player == null) return;
        ranking.remove(player);
        player.incrementLifetimeWins();
        ranking.add(player);
    }

    /// Keep registry within max size, then persist via the injected store
    @Override
    public void trimToMaxPlayers() {

        while (ranking.size() > MAX_PLAYERS) {
            Player lowest = ranking.pollLast();
            if (lowest != null) players.remove(lowest.getName());
        }

        store.saveAll(ranking);  // ranking is Iterable<Player>
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
        for (int i = 1; i <= 60; i++) {
            String newName = "PLAYER_" + i;
            if (!players.containsKey(newName)) return newName;
        }
        return "PLAYER_" + System.currentTimeMillis(); // fallback if all 60 slots taken
    }
}