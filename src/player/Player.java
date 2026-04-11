package player;

import auth.PasswordUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class Player implements Comparable<Player> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String name;
    private int lifetimeWins;

    // Both null means no password set
    private String passwordHash;
    private String passwordSalt;

    private final String joinDate;
    private String lastActive; // null = never played a session

    public Player(String name) {
        this.name = name;
        this.lifetimeWins = 0;
        this.passwordHash = null;
        this.passwordSalt = null;
        this.joinDate = LocalDate.now().format(FORMATTER);
        this.lastActive = null;
    }

    // Used by FilePlayerStore — hash/salt/lastActive may be null (backward compact)
    public Player(
            String name,
            int lifetimeWins,
            String passwordHash,
            String passwordSalt,
            String joinDate,
            String lastActive) {

        this.name = name;
        this.lifetimeWins = lifetimeWins;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.joinDate = (joinDate == null || joinDate.isEmpty())
                ? LocalDate.now().format(FORMATTER) : joinDate;
        this.lastActive = (lastActive == null || lastActive.isEmpty()) ? null : lastActive;
    }

    Player(Player player, String newName) {
        this.name = newName;
        this.lifetimeWins = player.getLifetimeWins();
        this.passwordHash = player.getPasswordHash();
        this.passwordSalt = player.getPasswordSalt();
        this.joinDate = player.getJoinDate();
        this.lastActive = player.getLastActiveExact();
    }

    // =====================================================
    // Date methods
    // =====================================================

    // Returns join date as "DD/MM/YYYY"
    public String memberSince() {
        return joinDate;
    }

    // Returns how many days ago this player joined
    public int daysOld() {
        LocalDate join = LocalDate.parse(joinDate, FORMATTER);
        return (int) ChronoUnit.DAYS.between(join, LocalDate.now());
    }

    // Called by PlayerCreator after player is resolved — marks today as last active
    public void markActive() {
        this.lastActive = LocalDate.now().format(FORMATTER);
    }

    // Returns "X days ago", "Today", or "Never played"
    public String lastActiveDisplay() {
        if (lastActive == null) return "Never played";
        LocalDate date = LocalDate.parse(lastActive, FORMATTER);
        long days = ChronoUnit.DAYS.between(date, LocalDate.now());
        if (days == 0) return "Today";
        return days + " days ago";
    }

    // For persistence only
    public String getJoinDate() {
        return joinDate;
    }

    public String getLastActive() {
        return lastActive != null ? lastActive : "";
    }

    String getLastActiveExact() {
        return lastActive;
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
    public String getPasswordHash() {
        return passwordHash != null ? passwordHash : "";
    }

    public String getPasswordSalt() {
        return passwordSalt != null ? passwordSalt : "";
    }

    // =====================================================
    // Standard methods
    // =====================================================

    @Override
    public int compareTo(Player o) {
        int winCompare = Integer.compare(o.lifetimeWins, this.lifetimeWins);
        if (winCompare != 0) return winCompare;
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

    void setLifetimeWins(int lifetimeWins) {
        this.lifetimeWins = lifetimeWins;
    }

    void removePassword() {
        this.passwordHash = null;
        this.passwordSalt = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}