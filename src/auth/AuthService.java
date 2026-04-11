package auth;

public interface AuthService {

    /**
     * Attempt to authenticate a player with the given password.
     * - If the player has no password, always returns true (open access).
     * - If the player is already locked this session, returns false immediately.
     * - Internally tracks failed attempts and locks after MAX_ATTEMPTS failures.
     */
    boolean authenticate(String playerName, String rawPassword);

    /**
     * Returns true if this player has been locked out for the current session.
     * Lock is in-memory only — resets on game restart.
     */
    boolean isLocked(String playerName);

    /**
     * Returns how many password attempt's remain before lockout.
     * Returns 0 if already locked.
     */
    int attemptsRemaining(String playerName);
}