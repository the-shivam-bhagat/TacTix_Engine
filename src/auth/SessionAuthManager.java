package auth;

import player.Registry;
import utility.Logger;

import java.util.HashMap;
import java.util.Map;

public final class SessionAuthManager implements AuthService {

    private static final int MAX_ATTEMPTS = 4;

    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, Boolean> locked = new HashMap<>();
    private final Registry registry;

    public SessionAuthManager(Registry registry) {
        this.registry = registry;
        Logger.info("Created auth service");
    }

    @Override
    public boolean authenticate(String playerName, String rawPassword) {
        if (isLocked(playerName)) {
            Logger.warn("Locked player attempted login: " + playerName);
            return false;
        }

        boolean success = registry.verifyPassword(playerName, rawPassword);

        if (success) {
            failedAttempts.remove(playerName);
            Logger.info("Auth success: " + playerName);
        } else {
            int attempts = failedAttempts.getOrDefault(playerName, 0) + 1;
            failedAttempts.put(playerName, attempts);
            Logger.warn("Auth failed: " + playerName + " | attempt " + attempts);

            if (attempts >= MAX_ATTEMPTS) {
                locked.put(playerName, true);
                Logger.warn("Account locked this session: " + playerName);
            }
        }

        return success;
    }

    @Override
    public boolean isLocked(String playerName) {
        return locked.getOrDefault(playerName, false);
    }

    @Override
    public int attemptsRemaining(String playerName) {
        int used = failedAttempts.getOrDefault(playerName, 0);
        return Math.max(0, MAX_ATTEMPTS - used);
    }
}
