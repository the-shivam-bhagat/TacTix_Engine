import java.util.*;

final class PlayerRegistry {
    // Fast lookup by player name
    final Map<String, Player> players;

    // Sorted ranking (highest wins first)
    final TreeSet<Player> ranking;

    public PlayerRegistry() {
        players = new HashMap<>();
        ranking = new TreeSet<>();
        // In the future: load from file/database
        loadPlayers();
    }

    static final int TOP_PLAYERS = 10;
    private static final int MAX_PLAYERS = 50;

    // Add a new player (if not already present)
    void addPlayer(Player player) {
        if (players.containsKey(player.name)) return;
        players.put(player.name, player);
        ranking.add(player);
    }

    private void removePlayer(Player player) {
        players.remove(player.name);
        ranking.remove(player);
    }

    boolean deletePlayerByName(String name) {
        Player player = players.get(name);
        if (player == null) return false;

        removePlayer(player);
        return true;
    }

    void incrementWin(Player player) {
        if (player == null) return;
        ranking.remove(player);             // remove first
        player.incrementLifetimeWins();     // update safely
        ranking.add(player);                // reinsert
    }

    void trimToMaxPlayers() {
        while (ranking.size() > MAX_PLAYERS) {
            Player lowest = ranking.pollLast();
            if (lowest != null) players.remove(lowest.name);
        }
        /* This function is called when game ends anyhow so
         * In this we have to call function to sore our data from registry */
        savePlayers();
    }

    private void loadPlayers() {
        //dp something here
    }

    private void savePlayers() {
        // do something here
    }
}