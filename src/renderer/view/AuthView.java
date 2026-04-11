package renderer.view;

import player.Player;

public interface AuthView {

    void requestPlayerName(int number);

    void showPlayerAlreadyInGame(String name);

    void showNewPlayerWelcome(Player player);

    void showReturningPlayerWelcome(Player player);

    void showAuthPasswordRequired(String playerName);

    void showAuthAttemptsRemaining(int remaining);

    void showAuthPasswordPrompt();

    void showAuthAccessGranted(String playerName);

    void showAuthIncorrectPassword();

    void showAuthAccountLocked(String playerName);

    void showAuthAccountLockedOnEntry(String playerName);

    void showAuthSetPasswordOffer();

    void showAuthPasswordRules();

    void showAuthConfirmPasswordPrompt();

    void showAuthPasswordMismatch();

    void showAuthPasswordSetSuccess();

    void showAuthNoPasswordSet();

    void showAuthValidationError(String error);

    // General purpose — kept here since auth flows use it heavily
    @SuppressWarnings("unused")
    void prompt(String message);
}
