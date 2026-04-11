package player.store;

import player.Player;
import utility.Config;
import utility.Logger;

import java.io.*;
import java.util.*;

public class FilePlayerStore implements PlayerStore {

    static final String FILE_NAME = Config.FileConfig.PLAYER_FILE_NAME;

    @Override
    public List<Player> loadAll() throws IOException {
        List<Player> result = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            Logger.warn("Player file not found, starting fresh");
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Logger.info("Reading players from file");
            String line;
            while ((line = reader.readLine()) != null) {
                Player p = decode(line);
                if (p != null) result.add(p);
            }
        }

        return result;
    }

    @Override
    public void saveAll(Iterable<Player> players) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            Random r = new Random();
            for (Player p : players) {
                writer.write(encode(p, r));
                writer.newLine();
            }
            Logger.info("Saving players to file");
        } catch (IOException e) {
            Logger.error("Failed to save players", e);
            throw new RuntimeException(e);
        }
    }

    // --------------------------------------------------
    // Format: name,wins,passwordHash,passwordSalt,joinDate,lastActive
    // All fields after wins are "" if not set
    // Backward compat: 2-field, 4-field, 5-field old files load cleanly
    // --------------------------------------------------

    private Player decode(String line) {
        if ((line.length() & 1) == 1) return null;

        StringBuilder sb = new StringBuilder(line.length() / 2);
        for (int i = 1; i < line.length(); i += 2) {
            int a = line.charAt(i - 1) - 32;
            int b = line.charAt(i) - 32;
            sb.append((char) (a + b));
        }

        // limit=6 — lastActive is last field, contains no commas
        String[] parts = sb.toString().split(",", 6);
        if (parts.length < 2) return null;

        try {
            String name = parts[0];
            int wins = Integer.parseInt(parts[1]);

            String passwordHash = (parts.length >= 3 && !parts[2].isEmpty()) ? parts[2] : null;
            String passwordSalt = (parts.length >= 4 && !parts[3].isEmpty()) ? parts[3] : null;
            String joinDate = (parts.length >= 5 && !parts[4].isEmpty()) ? parts[4] : null;
            String lastActive = (parts.length == 6 && !parts[5].isEmpty()) ? parts[5] : null;

            // Guard: hash without salt = corrupted → reset both
            if ((passwordHash == null) != (passwordSalt == null)) {
                Logger.warn("Corrupted password data for player: " + name + " — resetting");
                passwordHash = null;
                passwordSalt = null;
            }

            return new Player(name, wins, passwordHash, passwordSalt, joinDate, lastActive);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String encode(Player p, Random r) {
        String raw = p.getName() + ","
                + p.getLifetimeWins() + ","
                + p.getPasswordHash() + ","   // "" if no password
                + p.getPasswordSalt() + ","   // "" if no password
                + p.getJoinDate() + ","   // always present
                + p.getLastActive();          // "" if never played

        StringBuilder sb = new StringBuilder(raw.length() * 2);

        for (int i = 0; i < raw.length(); i++) {
            int cur = raw.charAt(i);
            int split = r.nextInt(cur - 1) + 1;
            sb.append((char) (split + 32));
            sb.append((char) ((cur - split) + 32));
        }

        return sb.toString();
    }
}