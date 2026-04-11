package renderer.view;

public interface EngineView extends
        AuthView,
        SetupView,
        AdminView,
        ExitCommandView,
        ManageCommandView,
        InputView {

    // GameEngine needs everything — extends all three
    // Plus its own exclusive methods below:

    void showSystemBoot();

    void showIntro();

    void showModuleHeader(int moduleNo, String title);

    void showFeatureLoadPrompt();

    void showFeatureLoading();

    void showFeatureSkipped();

    void showFeatures();

    void showBotSystemInit();

    void showBotsIntro();

    void showInstructionInit();

    void showInstructions();

    void showLeaderboardInit();

    void showSystemReady();

    void showGameStart(int gameNumber, String sessionType);

    void showPlayAgainPrompt();

    void showHistoryPrompt();

    void showUpdatedLeaderboardPrompt();

    void showRestartPrompt();

    void showRestartingMessage();

    void showEndingMessage();

    void showError(String message);

    void showStackTrace(String trace);

    void printLine();
}