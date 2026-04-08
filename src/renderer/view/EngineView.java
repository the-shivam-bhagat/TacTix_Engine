package renderer.view;

import player.Player;

public interface EngineView {
    // ================================
    // SYSTEM BOOT SEQUENCE
    // ================================
    void showSystemBoot();

    void showIntro();

    // ================================
    // MODULE 1 : FEATURES
    // ================================
    void showModuleHeader(int moduleNo, String title);

    void showFeatureLoadPrompt();

    void showFeatureLoading();

    void showFeatureSkipped();

    void showFeatures();

    // ================================
    // MODULE 2 : BOT SYSTEM
    // ================================
    void showBotSystemInit();

    void showBotsIntro();

    // ================================
    // MODULE 3 : INSTRUCTIONS
    // ================================
    void showInstructionInit();

    void showInstructions();

    // ================================
    // MODULE 4 : LEADERBOARD
    // ================================
    void showLeaderboardInit();

    // ================================
    // FINAL SYSTEM STATE
    // ================================
    void showSystemReady();

    void requestPlayerName(int number);

    void showPlayerAlreadyInGame(String name);

    void showSessionTypes();

    // ================================
    // GAME FLOW (POST INIT)
    // ================================
    void showSessionTypeInitialization(String sessionType);

    void showGameStart(int gameNumber, String sessionType);

    void showContinuePrompt();

    void showPlayAgainPrompt();

    void showHistoryPrompt();

    void showUpdatedLeaderboardPrompt();

    // ================================
    // SYSTEM CONTROL / ERROR HANDLING
    // ================================
    void showRestartPrompt();

    void showRestartingMessage();

    void showEndingMessage();

    void showExitMessage();

    void showError(String message);

    void showStackTrace(String trace); // optional but useful

    // ================================
    // UTILITY
    // ================================
    void prompt(String message);

    void printLine();

    void showNewPlayerWelcome(Player player);

    void showReturningPlayerWelcome(Player player);

    void showBotSelectionPrompt(int type);

    void showInvalidSessionChoice();

    void showInvalidBotChoice();

    void showInvalidCellChoice();

    void showBotIntroduction(String title, String[] TABLE_HEADERS, String[][] TABLE_DATA);

    void showAdminPanelNameRequest();

    void showAdminPanelEmptyNameError();

    void showAdminPanelPlayerDeleted(String name);

    void showAdminPanelPlayerNotFound(String name);

    void showAdminPanelDeleteAnother();

    void showAdminPanelSeperator();

    void showAdminPanelExitMessege();

    void requestAdminPassword();

    void showInvalidAdminPassword();

    void showContinueFromManageCmd();

    void showExitCommandMessage();

    void showBotsPanalViewMessage();

    void showBotChosen(String name, String value);
}