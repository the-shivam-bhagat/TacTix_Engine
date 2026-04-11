package auth;

import input.Input;
import player.Player;
import player.PlayerResult;
import player.Registry;
import renderer.view.AuthView;
import renderer.view.EngineView;
import utility.Logger;

public final class PlayerCreator {

    private final Input input;
    private final Registry playerRegistry;
    private final AuthView engineRenderer;
    private final AuthService authService;

    public PlayerCreator(Input input,
                         Registry playerRegistry,
                         EngineView engineRenderer,
                         AuthService authService) {
        this.input = input;
        this.playerRegistry = playerRegistry;
        this.engineRenderer = engineRenderer;
        this.authService = authService;
    }

    public Player createPlayer(String pre, int number) {
        String name = resolveName(pre, number);

        PlayerResult result = playerRegistry.getOrCreatePlayer(name);
        Player player = result.getPlayer();

        if (result.isNew()) {
            handleNewPlayer(player);
        } else {
            handleReturningPlayer(player, pre, number);
        }

        player.markActive();
        return player;
    }

    private String resolveName(String pre, int number) {
        while (true) {
            engineRenderer.requestPlayerName(number);
            String raw = input.readLine();
            if (raw == null) continue;

            String name = raw.trim().toUpperCase();

            if (number == 2 && pre.equals(name)) {
                engineRenderer.showPlayerAlreadyInGame(name);
                continue;
            }

            if (authService.isLocked(name)) {
                engineRenderer.showAuthAccountLockedOnEntry(name);
                continue;
            }

            return name;
        }
    }

    private void handleNewPlayer(Player player) {
        engineRenderer.showNewPlayerWelcome(player);
        offerPasswordSetup(player);
    }

    private void handleReturningPlayer(Player player, String pre, int number) {
        engineRenderer.showReturningPlayerWelcome(player);

        if (!player.hasPassword()) return;

        boolean authenticated = runAuthLoop(player);

        if (!authenticated) {
            engineRenderer.showAuthAccountLocked(player.getName());
            createPlayer(pre, number);
        }
    }

    private boolean runAuthLoop(Player player) {
        engineRenderer.showAuthPasswordRequired(player.getName());

        boolean firstAttempt = true;

        while (!authService.isLocked(player.getName())) {

            if (!firstAttempt) {
                engineRenderer.showAuthAttemptsRemaining(
                        authService.attemptsRemaining(player.getName())
                );
            }

            firstAttempt = false;

            engineRenderer.showAuthPasswordPrompt();

            String attempt = input.readRawLine();
            if (attempt == null) continue;

            if (authService.authenticate(player.getName(), attempt)) {
                engineRenderer.showAuthAccessGranted(player.getName());
                Logger.info("Player authenticated: " + player.getName());
                return true;
            }

            if (!authService.isLocked(player.getName())) {
                engineRenderer.showAuthIncorrectPassword();
            }
        }

        return false;
    }

    private void offerPasswordSetup(Player player) {
        engineRenderer.showAuthSetPasswordOffer();

        String choice = input.readLine();

        if (choice == null || choice.isEmpty()
                || Character.toUpperCase(choice.charAt(0)) != 'Y') {
            engineRenderer.showAuthNoPasswordSet();
            return;
        }

        runPasswordSetupLoop(player);
    }

    private void runPasswordSetupLoop(Player player) {
        while (true) {
            engineRenderer.showAuthPasswordRules();

            String newPassword = input.readRawLine();

            if (newPassword == null || newPassword.isEmpty()) {
                engineRenderer.showAuthNoPasswordSet();
                return;
            }

            String error = PasswordUtil.getValidationError(newPassword);
            if (error != null) {
                engineRenderer.showAuthValidationError(error);
                continue;
            }

            engineRenderer.showAuthConfirmPasswordPrompt();
            String confirm = input.readRawLine();

            if (confirm == null || !confirm.equals(newPassword)) {
                engineRenderer.showAuthPasswordMismatch();
                continue;
            }

            playerRegistry.setPassword(player.getName(), newPassword);
            engineRenderer.showAuthPasswordSetSuccess();
            Logger.info("Password set for new player: " + player.getName());
            return;
        }
    }
}