package player;

import auth.PasswordUtil;

public final class Player implements Comparable<Player> {

    private final String name;
    private int lifetimeWins;

    // Both null means no password set
    private String passwordHash;
    private String passwordSalt;

    public Player(String name) {
        this.name = name;
        this.lifetimeWins = 0;
        this.passwordHash = null;
        this.passwordSalt = null;
    }

    // Used by FilePlayerStore — hash/salt may be null (backward compact + no password)
    public Player(String name, int lifetimeWins, String passwordHash, String passwordSalt) {
        this.name = name;
        this.lifetimeWins = lifetimeWins;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    // =====================================================
    // Password logic — all via PasswordUtil
    // =====================================================

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasPassword() {
        return passwordHash != null && passwordSalt != null
                && !passwordHash.isEmpty() && !passwordSalt.isEmpty();
    }

    // Called by PlayerRegistry.setPassword()
    void setPassword(String rawPassword) {
        this.passwordSalt = PasswordUtil.generateSalt();
        this.passwordHash = PasswordUtil.hash(rawPassword, this.passwordSalt);
    }

    // Called by PlayerRegistry.verifyPassword()
    boolean matchesPassword(String rawPassword) {
        if (!hasPassword()) return true; // no password = open access
        String attemptHash = PasswordUtil.hash(rawPassword, this.passwordSalt);
        return passwordHash.equals(attemptHash);
    }

    // For persistence only — FilePlayerStore reads these directly
    public String getPasswordHash() { return passwordHash != null ? passwordHash : ""; }
    public String getPasswordSalt() { return passwordSalt != null ? passwordSalt : ""; }

    // =====================================================
    // Standard methods
    // =====================================================

    @Override
    public int compareTo(Player o) {
        int winCompare = Integer.compare(o.lifetimeWins, this.lifetimeWins);
        if (winCompare != 0) return winCompare;
        return this.name.compareTo(o.name);
    }

    @Override public String toString() { return name; }

    public String getName() { return name; }
    public int getLifetimeWins() { return lifetimeWins; }

    void incrementLifetimeWins() { lifetimeWins++; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return name.equals(player.name);
    }

    @Override public int hashCode() { return name.hashCode(); }
}