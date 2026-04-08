package player.store;

import player.Player;
import utility.Config;
import utility.Logger;

import java.io.*;
import java.util.*;

/**
 * File-based implementation of PlayerStore.
 * Name = FilePlayerStore: tells you WHAT it is (a Store) and HOW (File).
 * Swap for DatabasePlayerStore or MemoryPlayerStore without touching PlayerRegistry.
 */
public class FilePlayerStore implements PlayerStore {

    static final String FILE_NAME = Config.PLAYER_FILE_NAME;

    @Override
    public List<Player> loadAll() throws IOException {
        List<Player> result = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            Logger.warn("Player file not found, starting fresh");
            return result;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Logger.info("Reading players from file");

        String line;
        while ((line = reader.readLine()) != null) {
            Player p = decode(line);
            if (p != null) result.add(p);
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
        }
    }

    // --------------------------------------------------
    // Encoding / decoding (private — callers don't care)
    // --------------------------------------------------

    private Player decode(String line) {
        if ((line.length() & 1) == 1) return null;
        StringBuilder sb = new StringBuilder(line.length() / 2);

        for (int i = 1; i < line.length(); i += 2) {
            int a = line.charAt(i - 1) - 32;
            int b = line.charAt(i) - 32;
            sb.append((char) (a + b));
        }

        String[] parts = sb.toString().split(",");
        if (parts.length != 2) return null;

        try {
            return new Player(parts[0], Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String encode(Player p, Random r) {
        String raw = p.getName() + "," + p.getLifetimeWins();
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