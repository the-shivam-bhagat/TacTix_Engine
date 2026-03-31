package renderer;

import myUtil.Strings;

import java.io.PrintStream;

public class EngineRenderer {

    private final PrintStream output;

    public EngineRenderer(PrintStream output) {
        this.output = output;
    }

    public void showIntro() {
        output.println(Strings.INTRO_STRING);
    }

    public void showFeatures() {
        output.println(Strings.FEATURES_STRING);
    }

    public void showInstructions() {
        output.println(Strings.ISTRUCTION_STRING);
    }

    public void prompt(String message) {
        output.print(message);
    }

    public void showGameStart(int gameNumber) {
        output.printf("%n⚔️ Game %d — Let the battle begin!%n", gameNumber);
    }

    public void showContinuePrompt() {
        output.print(" ..... (Press ENTER to continue) ");
    }

    public void showPlayAgainPrompt() {
        output.print("\n🎮 Play another game? (Y/N): ");
    }

    public void showHistoryPrompt() {
        output.print("\nLet's see all Game Session & Results ..... (Press ENTER to continue)");
    }

    public void showUpdatedLeaderboardPrompt() {
        output.print("Let's take a look at 🏆 Updated Global Leaderboard..... (Press ENTER to continue)");
    }

    public void showRestartPrompt() {
        output.printf("""
                
                ⚠ A system error occurred.
                Do you want to restart the game (Y/N) ? :%1s""", "");
    }

    public void showRestartingMessage() {
        output.print("\nRestarting the game..... (Press ENTER to continue) ");
    }

    public void showEndingMessage() {
        output.print("🎮 Session complete. Thank you for playing! (Press ENTER to exit) ");
    }

    public void showExitMessage() {
        output.print("\nProgram terminated. Thank you for playing! (Press ENTER to exit) ");
    }

    public void printLine() {
        output.println();
    }

}