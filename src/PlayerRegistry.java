import java.io.*;
import java.util.*;

final class PlayerRegistry {

    /// Fast lookup by player name
    final Map<String, Player> players;

    /// Sorted ranking (highest wins first)
    final TreeSet<Player> ranking;

    /// File used for persistence
    private static final String FILE_NAME = "players.dat";

    public PlayerRegistry() {
        players = new HashMap<>();
        ranking = new TreeSet<>();

        loadPlayers();   // load saved players
    }

    static final int TOP_PLAYERS = 10;
    private static final int MAX_PLAYERS = 50;


    /// Add a new player (if not already present)
    void addPlayer(Player player) {
        if (players.containsKey(player.name)) return;

        players.put(player.name, player);
        ranking.add(player);
    }


    /// Internal removal helper
    private void removePlayer(Player player) {
        players.remove(player.name);
        ranking.remove(player);
    }


    /// Delete player by name
    boolean deletePlayerByName(String name) {
        Player player = players.get(name);
        if (player == null) return false;

        removePlayer(player);
        return true;
    }



    /// Safely update win count
    void incrementWin(Player player) {
        if (player == null) return;
        ranking.remove(player);
        player.incrementLifetimeWins();
        ranking.add(player);
    }

    /// Keep registry within max size
    void trimToMaxPlayers() {

        while (ranking.size() > MAX_PLAYERS) {
            Player lowest = ranking.pollLast();
            if (lowest != null) players.remove(lowest.name);
        }

        savePlayers();
    }


    // =====================================================
    // LOAD PLAYERS FROM FILE
    // =====================================================

    private void loadPlayers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // first run → no file yet

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) continue;

                String name = parts[0];
                int wins = Integer.parseInt(parts[1]);

                Player p = new Player(name, wins);
                players.put(name, p);
                ranking.add(p);
            }

        } catch (IOException e) {
            System.err.println("Failed to load players from file.");
            GameEngine.restart();
        }
    }


    // =====================================================
    // SAVE PLAYERS TO FILE
    // =====================================================

    private void savePlayers() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Player p : players.values()) {
                writer.write(p.name + "," + p.getLifetimeWins());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Failed to save Players from this Game.");
        }
    }
}