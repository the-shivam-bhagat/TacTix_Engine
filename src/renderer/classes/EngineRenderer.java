package renderer.classes;

import player.Player;
import renderer.view.EngineView;
import utility.Strings;

import java.io.PrintStream;

public class EngineRenderer implements EngineView {

    private final PrintStream output;

    public EngineRenderer(PrintStream output) {
        this.output = output;
    }

    // ================================
    // SYSTEM BOOT SEQUENCE
    // ================================

    @Override
    public void showSystemBoot() {
        output.print("\n> [SYSTEM] Initializing Tic-Tac-Toe AI Engine\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showIntro() {
        output.println(Strings.INTRO_STRING);
    }

    // ================================
    // MODULE FLOW (IN ORDER)
    // ================================

    @Override
    public void showModuleHeader(int moduleNo, String title) {
        output.printf("""
                
                [MODULE %d] %s
                """, moduleNo, title);
    }

    @Override
    public void showFeatureLoadPrompt() {
        output.print("> Load system features? (Y/N): ");
    }

    @Override
    public void showFeatureLoading() {
        output.print("\n> [SETUP] Loading feature definitions...");
    }

    @Override
    public void showFeatureSkipped() {
        output.printf("> [SKIP] Feature module skipped%n");
    }

    @Override
    public void showFeatures() {
        output.println(Strings.FEATURES_STRING);
    }

    @Override
    public void showBotSystemInit() {
        output.print("> [SETUP] Initializing bot architecture...\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showBotsIntro() {
        output.println(Strings.BOTS_INTRODUCTION_PANEL);
    }

    @Override
    public void showInstructionInit() {
        output.print("> [SETUP] Loading gameplay instructions...\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showInstructions() {
        output.println(Strings.INSTRUCTION_STRING);
    }

    @Override
    public void showLeaderboardInit() {
        output.print("> [SETUP] Fetching latest rankings...\n> [INPUT] Press ENTER to continue");
    }

    // ================================
    // FINAL SYSTEM STATE
    // ================================

    @Override
    public void showSystemReady() {
        output.print("""
                
                > [SYSTEM] Ready
                > [START] Launching game engine...
                > [INPUT] Press ENTER to continue""");
    }

    // ================================
    // SESSION SETUP FLOW
    // ================================

    @Override
    public void showSessionTypes() {
        output.print("""
                
                ╔════════════════════════════════════╗
                ║ << SESSION CONFIGURATION MODULE >> ║
                ╠════════════════════════════════════╣
                ║  Available session types:          ║
                ║                                    ║
                ║    1) PLAYER vs PLAYER             ║
                ║    2) PLAYER vs BOT                ║
                ║    3) BOT    vs BOT                ║
                ║                                    ║
                ╚════════════════════════════════════╝
                
                > [INPUT] Select session type (1–3):\s"""
        );
    }

    @Override
    public void showSessionTypeInitialization(String sessionType) {
        output.printf("%n> [SETUP] Initializing → %s%n", sessionType);
    }

    @Override
    public void showBotSelectionPrompt(int type) {
        switch (type) {
            case 0 -> output.printf("%n> [INPUT] Choose a bot");
            case 1 -> output.printf("%n> [INPUT] Choose First bot");
            case 2 -> output.printf("%n> [INPUT] Choose Second bot");
        }
        output.printf("%n> [INPUT] Enter bot level (0–5): ");
    }

    @Override
    public void showBotsPanelViewMessage() {
        output.printf("%n> [SYSTEM] Showing Bots...%n");
    }

    @Override
    public void showBotIntroduction(String title, String[] TABLE_HEADERS, String[][] TABLE_DATA) {
        int[] widths = new int[TABLE_HEADERS.length];
        for (int i = 0; i < TABLE_HEADERS.length; i++)
            widths[i] = TABLE_HEADERS[i].length();

        for (String[] row : TABLE_DATA)
            for (int i = 0; i < TABLE_HEADERS.length; i++)
                if (row[i].length() > widths[i]) widths[i] = row[i].length();

        for (int i = 0; i < widths.length; i++) widths[i] += 1;

        int lenSum = 0;
        for (int len : widths) lenSum += len;
        if (lenSum < title.length() + 2) {
            int perDiff = (title.length() - lenSum) / widths.length + 1;
            for (int i = 0; i < widths.length; i++) widths[i] += perDiff;
        }

        int innerWidth = 0;
        for (int w : widths) innerWidth += w + 3;
        innerWidth = Math.max(innerWidth, title.length() + 2);
        innerWidth -= 1;

        output.println();
        output.println("╔" + "═".repeat(innerWidth) + "╗");
        int sidePad = (innerWidth - title.length()) / 2;
        output.println("║" +
                " ".repeat(sidePad) + title +
                " ".repeat(Math.max(innerWidth - title.length() - sidePad, 0)) + "║");
        output.println("╠" + "═".repeat(innerWidth) + "╣");

        var header = new StringBuilder("║");
        for (int i = 0; i < TABLE_HEADERS.length; i++) {
            header.append(String.format(" %-" + widths[i] + "s │", TABLE_HEADERS[i]));
        }
        header.setCharAt(header.length() - 1, '║');
        output.println(header);

        for (String[] row : TABLE_DATA) {
            StringBuilder divider = new StringBuilder("╟");
            for (int i = 0; i < widths.length; i++) {
                divider.append("─".repeat(widths[i] + 2));
                divider.append(i < widths.length - 1 ? "┼" : "╢");
            }
            output.println(divider);

            StringBuilder line = new StringBuilder("║");
            for (int i = 0; i < TABLE_HEADERS.length; i++)
                line.append(String.format(" %-" + widths[i] + "s │", row[i]));
            line.setCharAt(line.length() - 1, '║');
            output.println(line);
        }

        output.println("╚" + "═".repeat(innerWidth) + "╝");
        output.println();
    }

    @Override
    public void showBotChosen(String name, String value) {
        output.printf("%n> [SYSTEM] Bot %s has been selected as %s%n", name, value);
    }

    // ================================
    // GAME FLOW
    // ================================

    @Override
    public void showGameStart(int gameNumber, String sessionType) {
        output.printf("%n%n> [GAME] %s | Game %d started", sessionType, gameNumber);
    }

    @Override
    public void showContinuePrompt() {
        output.printf("%n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showPlayAgainPrompt() {
        output.print("\n> [INPUT] Play another game? (Y/N): ");
    }

    @Override
    public void showHistoryPrompt() {
        output.print("\n> [INFO] Viewing game session history\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showUpdatedLeaderboardPrompt() {
        output.print("> [INFO] Viewing updated global leaderboard\n> [INPUT] Press ENTER to continue");
    }

    // ================================
    // INPUT ERRORS
    // ================================

    @Override
    public void showInvalidSessionChoice() {
        output.print("""
                > [ERROR] Invalid selection.
                > [INPUT] Enter a value between 1 and 3:\s""");
    }

    @Override
    public void showInvalidBotChoice() {
        output.print("""
                > [ERROR] Invalid selection.
                > [Input] Enter a value between 0 and 5:\s""");
    }

    @Override
    public void showInvalidCellChoice() {
        output.print("> [ERROR] Invalid move. Enter an unoccupied cell (1–9): ");
    }

    // ================================
    // ADMIN FLOW
    // ================================

    @Override
    public void requestAdminPassword() {
        output.printf("%n> [INPUT] Enter admin password: ");
    }

    @Override
    public void showInvalidAdminPassword() {
        output.printf("> [ERROR] Invalid password%n");
    }

    @Override
    public void showAdminPanelEmptyNameError() {
        output.printf("> [ERROR] ⚠ Player name cannot be empty.%n");
    }

    @Override
    public void showAdminPanelPlayerDeleted(String name) {
        output.printf("> [RESULT] Player '%s' deleted successfully%n", name);
    }

    @Override
    public void showAdminPanelPlayerNotFound(String name) {
        output.printf("> [ERROR] Player '%s' not found%n", name);
    }

    @Override
    public void showAdminPanelSeparator() {
        output.printf("> [INFO] " + "-".repeat(50) + "%n");
    }

    @Override
    public void showAdminPanelExitMessage() {
        output.printf("%n> [SYSTEM] Exiting player management%n");
    }

    @Override
    public void showContinueFromManageCmd() {
        output.print("""
                
                > [SYSTEM] Returning to game
                
                """);
    }

    // ── Selection ─────────────────────────────────────────────────────

    @Override
    public void showAdminMenu() {
        output.print("""
            
            > [INFO] Select player from menu to perform actions on
            > [ADMIN] Select player by:
                [1] Name
                [2] Rank
                [3] Refresh player table
                [0] Exit admin panel
            
            > [INPUT] Choice:\s""");
    }

    @Override
    public void showAdminSelectByNamePrompt() {
        output.print("\n> [INPUT] Enter player name: ");
    }

    @Override
    public void showAdminSelectByRankPrompt() {
        output.print("\n> [INPUT] Enter player rank: ");
    }

    @Override
    public void showAdminPlayerNotFound(String identifier) {
        output.printf("> [ERROR] No player found for: '%s'%n", identifier);
    }

    @Override
    public void showAdminAnotherAction() {
        output.print("\n> [INPUT] Perform another action? (Y/N): ");
    }

// ── Operation menu ────────────────────────────────────────────────

    @Override
    public void showAdminOperationMenu(String playerName) {
        output.printf("""
                
                > [ADMIN] Selected player: %s
                > [ADMIN] Choose operation:
                    [1] Change name
                    [2] Manage password
                    [3] Set lifetime wins
                    [4] Delete player
                    [0] Back
                
                > [INPUT] Choice:\s""", playerName);
    }

// ── Change name ───────────────────────────────────────────────────

    @Override
    public void showAdminChangeNamePrompt(String currentName) {
        output.printf("%n> [INPUT] Enter new name for '%s': ", currentName);
    }

    @Override
    public void showAdminRenameSuccess(String oldName, String newName) {
        output.printf("> [ADMIN] Player '%s' renamed to '%s'%n", oldName, newName);
    }

    @Override
    public void showAdminNameTaken(String name) {
        output.printf("> [ERROR] Name '%s' is already taken. Choose another.%n", name);
    }

// ── Password management ───────────────────────────────────────────

    @Override
    public void showAdminPasswordMenuHasPassword(String playerName) {
        output.printf("""
                
                > [ADMIN] '%s' currently has a password.
                    [1] Change password
                    [2] Remove password
                    [0] Back
                
                > [INPUT] Choice:\s""", playerName);
    }

    @Override
    public void showAdminPasswordMenuNoPassword(String playerName) {
        output.printf("""
                
                > [ADMIN] '%s' has no password set.
                    [1] Add password
                    [0] Back
                
                > [INPUT] Choice:\s""", playerName);
    }

    @Override
    public void showAdminPasswordInputPrompt(boolean isChange) {
        output.printf("""
                        
                        > [INFO]  Rules: 4-32 chars, no spaces, letters/digits/symbols only.
                        
                        > [INPUT] Enter%s password (or press ENTER to cancel):\s""",
                isChange ? " new" : "");
    }

    @Override
    public void showAdminPasswordConfirmPrompt(boolean isChange) {
        output.printf("%n> [INPUT] Confirm%s password: ", isChange ? " new" : "");
    }

    @Override
    public void showAdminPasswordMismatch() {
        output.print("> [ERROR] Passwords do not match. Try again.\n");
    }

    @Override
    public void showAdminPasswordChanged(String name) {
        output.printf("> [ADMIN] Password changed for '%s'%n", name);
    }

    @Override
    public void showAdminPasswordRemoved(String name) {
        output.printf("> [ADMIN] Password removed for '%s' — account is now open%n", name);
    }

    @Override
    public void showAdminPasswordAdded(String name) {
        output.printf("> [ADMIN] Password added for '%s'%n", name);
    }

    @Override
    public void showAdminValidationError(String error) {
        output.printf("> [ERROR] %s%n", error);
    }

// ── Set wins ──────────────────────────────────────────────────────

    @Override
    public void showAdminWinsCurrentAndPrompt(String name, int currentWins) {
        output.printf("""
                
                > [INFO]  Current wins for '%s': %d
                
                > [INPUT] Enter new win count:\s""", name, currentWins);
    }

    @Override
    public void showAdminWinsUpdated(String name, int wins) {
        output.printf("> [ADMIN] Lifetime wins for '%s' set to %d%n", name, wins);
    }

    @Override
    public void showAdminWinsNegativeError() {
        output.print("%n> [ERROR] Win count cannot be negative.\n");
    }

// ── Delete ────────────────────────────────────────────────────────

    @Override
    public void showAdminDeleteConfirmPrompt(String playerName) {
        output.printf("%n> [ADMIN] Are you sure you want to delete '%s'? (Y/N): ",
                playerName);
    }

// ── General ───────────────────────────────────────────────────────

    @Override
    public void showAdminInvalidInput() {
        output.print("> [ERROR] Invalid input. Please try again.\n");
    }

    @Override
    public void showAdminActionCancelled() {
        output.print("> [ADMIN] Action cancelled.\n");
    }

    @Override
    public void showAdminPanelContinuePrompt() {
        output.print("\n> [INPUT] Press ENTER to continue to player management");
    }

    @Override
    public void showPlayerTableIsRefreshed() {
        output.print("> [INFO] Player table is refreshed.\n");
        showAdminPanelSeparator();
    }

    // ================================
    // SYSTEM CONTROL / ERROR HANDLING
    // ================================

    @Override
    public void showRestartPrompt() {
        output.print("""
                
                > [ERROR] A system error occurred
                > [INPUT] Restart the game? (Y/N):\s""");
    }

    @Override
    public void showRestartingMessage() {
        output.print("\n> [SYSTEM] Restarting game\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showEndingMessage() {
        output.print("> [SYSTEM] Session complete\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showExitMessage() {
        output.print("\n> [SYSTEM] Program terminated\n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showExitCommandMessage() {
        output.printf("%n> [SYSTEM] Exiting game. Goodbye%n");
    }

    @Override
    public void showError(String message) {
        output.println("\n> [ERROR] " + message + "\n");
    }

    @Override
    public void showStackTrace(String trace) {
        output.println(trace);
    }

    @Override
    public void requestPlayerName(int number) {
        output.printf("%n> [INPUT] Enter name for Player %d: ", number);
    }

    @Override
    public void showPlayerAlreadyInGame(String name) {
        output.printf("> [ERROR] Player %s is already in the game!%n", name);
    }

    // ================================
    // PLAYER FEEDBACK
    // ================================

    private boolean flip = Math.random() < 0.5;

    @Override
    public void showNewPlayerWelcome(Player player) {
        output.printf(
                flip ? Strings.WELCOME_NEW_PLAYER_1
                        : Strings.WELCOME_NEW_PLAYER_2,
                player.getName(), player.getLifetimeWins()
        );
        flip = !flip;
    }

    @Override
    public void showReturningPlayerWelcome(Player player) {
        output.printf(
                flip ? Strings.WELCOME_REGISTERED_PLAYER_1
                        : Strings.WELCOME_REGISTERED_PLAYER_2,
                player.getName(), player.getLifetimeWins()
        );
        flip = !flip;
    }

    // ================================
    // Auth renderers
    // ================================

    @Override
    public void showAuthPasswordRequired(String playerName) {
        output.printf("%n> [AUTH] Password required for: %s%n", playerName);
    }

    @Override
    public void showAuthAttemptsRemaining(int remaining) {
        output.printf("> [AUTH] Attempts remaining: %d%n", remaining);
    }

    @Override
    public void showAuthPasswordPrompt() {
        output.print("\n> [INPUT] Enter password: ");
    }

    @Override
    public void showAuthAccessGranted(String playerName) {
        output.printf("> [AUTH] Access granted. Welcome back, %s%n", playerName);
    }

    @Override
    public void showAuthIncorrectPassword() {
        output.print("> [AUTH] Incorrect password.\n");
    }

    @Override
    public void showAuthAccountLocked(String playerName) {
        output.printf("%n> [AUTH] Account '%s' is now locked for this session.%n" +
                        "> [AUTH] You cannot access this account until the game restarts.%n",
                playerName);
    }

    @Override
    public void showAuthAccountLockedOnEntry(String playerName) {
        output.printf("%n> [AUTH] Account '%s' is locked for this session.%n" +
                "> [AUTH] Please use a different name.%n", playerName);
    }

    @Override
    public void showAuthSetPasswordOffer() {
        output.print("\n> [AUTH] Would you like to set a password for your account? (Y/N): ");
    }

    @Override
    public void showAuthPasswordRules() {
        output.print("""
                
                > [INFO]  Rules: 4-32 chars, no spaces, letters/digits/symbols only
                
                > [INPUT] Enter new password (or press ENTER to skip):\s""");
    }

    @Override
    public void showAuthConfirmPasswordPrompt() {
        output.print("\n> [INPUT] Confirm your password: ");
    }

    @Override
    public void showAuthPasswordMismatch() {
        output.print("> [ERROR] Passwords do not match. Please try again.\n");
    }

    @Override
    public void showAuthPasswordSetSuccess() {
        output.print("> [AUTH] Password set successfully.\n");
    }

    @Override
    public void showAuthNoPasswordSet() {
        output.print("\n> [AUTH] No password set — account remains open.\n");
    }

    @Override
    public void showAuthValidationError(String error) {
        output.printf("> [ERROR] %s Please try again.%n", error);
    }

    // ================================
    // UTILITY
    // ================================

    @Override
    public void prompt(String message) {
        output.print(message);
    }

    @Override
    public void printLine() {
        output.println();
    }
}