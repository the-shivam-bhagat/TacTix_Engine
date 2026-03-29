import java.io.*;
import java.util.*;

final class PlayerRegistry {

    /// Fast lookup by player name
    private final Map<String, Player> players;

    /// Sorted ranking (highest wins first)
    private final TreeSet<Player> ranking;

    /// File used for persistence
    private static final String FILE_NAME = "players.dat";

    private PlayerRegistry() {
        players = new HashMap<>();
        ranking = new TreeSet<>();

        loadPlayers();   // load saved players
    }

    static final int TOP_PLAYERS = 10;
    private static final int MAX_PLAYERS = 50;

    private static volatile PlayerRegistry instance;

    public static PlayerRegistry getPlayerRegistry() {
        if (instance == null) {
            synchronized (PlayerRegistry.class) {
                if (instance == null) {
                    instance = new PlayerRegistry();
                }
            }
        }
        return instance;
    }

    /// Add a new player (if not already present)
    public void addPlayer(Player player) {
        if (players.containsKey(player.name)) return;

        players.put(player.name, player);
        ranking.add(player);
    }

    // for greeting message
    private static boolean flip = (int) Math.floor(Math.random() * 100) % 2 == 0;

    /// if players exists returns it, if not then create and return
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
        // no name;
        if (name == null || name.isEmpty()) {
            for (int i = 1; i <= 60; i++) {
                String newName = "PLAYER_" + i;
                if (!players.containsKey(newName)) {
                    name = newName;
                    break;
                }
            }
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
        players.remove(player.name);
        ranking.remove(player);
    }


    /// Delete player by name
    public boolean deletePlayerByName(String name) {
        Player player = players.get(name);
        if (player == null) return false;

        removePlayer(player);
        return true;
    }


    /// Safely update win count
    public void incrementWin(Player player) {
        if (player == null) return;
        ranking.remove(player);
        player.incrementLifetimeWins();
        ranking.add(player);
    }

    /// Keep registry within max size
    public void trimToMaxPlayers() {

        while (ranking.size() > MAX_PLAYERS) {
            Player lowest = ranking.pollLast();
            if (lowest != null) players.remove(lowest.name);
        }

        savePlayers();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public int size() {
        return players.size();
    }

    public Iterator<Player> iterator() {
        return ranking.iterator();
    }

    public Player peekTopPlayer() {
        return ranking.first();
    }


    // =====================================================
    // LOAD PLAYERS FROM FILE
    // =====================================================

    private void loadPlayers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ((line.length() & 1) == 1) continue;
                StringBuilder sb = new StringBuilder(line.length() / 2);

                for (int i = 1; i < line.length(); i += 2) {
                    int a = line.charAt(i - 1) - 32;
                    int b = line.charAt(i) - 32;
                    sb.append((char) (a + b));
                }

                String[] parts = sb.toString().split(",");
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
            Random r = new Random();
            for (Player p : players.values()) {
                String line = p.name + "," + p.getLifetimeWins();
                StringBuilder sb = new StringBuilder(line.length() * 2);

                for (int i = 0; i < line.length(); i++) {
                    int cur = line.charAt(i);
                    int split = r.nextInt(cur - 1) + 1; // range 0 < split < cur;
                    // add 32 to not generate (\0, \r, \n, etc.).
                    int a = split + 32;
                    int b = (cur - split) + 32;

                    writer.write((char) a);
                    writer.write((char) b);
                }

                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Failed to save Players from this Game.");
        }
    }
}