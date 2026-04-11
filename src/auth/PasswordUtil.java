package auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public final class PasswordUtil {

    private PasswordUtil() {}

    // ================================================================
    // SALT + HASH
    // ================================================================

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return bytesToHex(salt);
    }

    public static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = password + salt;
            byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }

    // ================================================================
    // VALIDATION
    // Rules: 4–32 chars, no spaces, printable ASCII only (33–126)
    // ================================================================

    @SuppressWarnings("unused")
    public static boolean isValid(String password) {
        return getValidationError(password) == null;
    }

    /**
     * Returns a human-readable error message if the password is invalid.
     * Returns null if the password passes all rules.
     */
    public static String getValidationError(String password) {
        if (password == null || password.isEmpty())
            return "Password cannot be empty.";

        if (password.length() < 4)
            return "Password must be at least 4 characters.";

        if (password.length() > 32)
            return "Password must be at most 32 characters.";

        for (char c : password.toCharArray()) {
            // Printable ASCII: 33–126 — space (32) is excluded naturally
            if (c < 33 || c > 126)
                return "Password must not contain spaces or non-printable characters.";
        }

        return null; // valid
    }

    // ================================================================
    // PRIVATE HELPER
    // ================================================================

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes)
            hex.append(String.format("%02x", b));
        return hex.toString();
    }
}