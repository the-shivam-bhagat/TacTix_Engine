/// Player class.
public final class Player implements Comparable<Player> {

    private final String name;
    private int lifetimeWins;

    public Player(String name) {
        this.name = name;
        lifetimeWins = 0;
    }

    /// Package-private — only used by FilePlayerStore when loading saved players
    Player(String name, int lifetimeWins) {
        this.name = name;
        this.lifetimeWins = lifetimeWins;
    }

    @Override
    public int compareTo(Player o) {
        // Higher wins first
        int winCompare = Integer.compare(o.lifetimeWins, this.lifetimeWins);
        if (winCompare != 0) return winCompare;
        // If wins equal → compare names
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getLifetimeWins() {
        return lifetimeWins;
    }

    void incrementLifetimeWins() {
        lifetimeWins++;
    }
}