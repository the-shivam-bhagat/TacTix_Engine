package renderer;

import renderer.view.EngineView;
import utility.Strings;

import java.io.PrintStream;

public class EngineRenderer implements EngineView {

    private final PrintStream output;

    public EngineRenderer(PrintStream output) {
        this.output = output;
    }

    @Override
    public void showIntro() {
        output.println(Strings.INTRO_STRING);
    }

    @Override
    public void showFeatures() {
        output.println(Strings.FEATURES_STRING);
    }

    @Override
    public void showInstructions() {
        output.println(Strings.ISTRUCTION_STRING);
    }

    @Override
    public void prompt(String message) {
        output.print(message);
    }

    @Override
    public void showGameStart(int gameNumber) {
        output.printf("%n⚔️ Game %d — Let the battle begin!%n", gameNumber);
    }

    @Override
    public void showContinuePrompt() {
        output.print(" ..... (Press ENTER to continue) ");
    }

    @Override
    public void showPlayAgainPrompt() {
        output.print("\n🎮 Play another game? (Y/N): ");
    }

    @Override
    public void showHistoryPrompt() {
        output.print("\nLet's see all Game Session & Results ..... (Press ENTER to continue)");
    }

    @Override
    public void showUpdatedLeaderboardPrompt() {
        output.print("Let's take a look at 🏆 Updated Global Leaderboard..... (Press ENTER to continue)");
    }

    @Override
    public void showRestartPrompt() {
        output.printf("""
                
                ⚠ A system error occurred.
                Do you want to restart the game (Y/N) ? :%1s""", "");
    }

    @Override
    public void showRestartingMessage() {
        output.print("\nRestarting the game..... (Press ENTER to continue) ");
    }

    @Override
    public void showEndingMessage() {
        output.print("🎮 Session complete. Thank you for playing! (Press ENTER to exit) ");
    }

    @Override
    public void showExitMessage() {
        output.print("\nProgram terminated. Thank you for playing! (Press ENTER to exit) ");
    }

    @Override
    public void printLine() {
        output.println();
    }

    @Override
    public void showError(String message) {
        output.println("\n⚠ ERROR: " + message + "\n");
    }

    @Override
    public void showStackTrace(String trace) {
        output.println(trace);
    }
}