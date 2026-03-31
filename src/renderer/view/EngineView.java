package renderer.view;

public interface EngineView {
    void showIntro();

    void showFeatures();

    void showInstructions();

    void prompt(String message);

    void showGameStart(int gameNumber);

    void showContinuePrompt();

    void showPlayAgainPrompt();

    void showHistoryPrompt();

    void showUpdatedLeaderboardPrompt();

    void showRestartPrompt();

    void showRestartingMessage();

    void showEndingMessage();

    void showExitMessage();

    void printLine();

    void showError(String message);

    void showStackTrace(String trace); // optional but useful
}