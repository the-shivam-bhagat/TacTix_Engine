package renderer.classes;

import player.Player;
import renderer.view.SessionView;
import utility.Logger;
import utility.Strings;

import java.io.PrintStream;

public class SessionRenderer implements SessionView {

    private final PrintStream output;

    public SessionRenderer(PrintStream output) {
        this.output = output;
    }

    // ================================
    // ROUND FLOW (IN ORDER)
    // ================================

    @Override
    public void showRoundStart(int round) {
        output.printf("%n> [ROUND] Round %d is about to begin%n> [INPUT] Press ENTER to continue", round);
    }

    @Override
    public void showFirstMovePrompt(String first, String second) {
        output.printf("""
                        
                        > [MATCH] %s vs %s
                        > [INPUT] %s, play first? (Y/N):\s""",
                first, second, first
        );
    }

    @Override
    public void showMovePrompt(Player player, char mark) {
        output.printf("> [INPUT] %s, enter your move (%c) [1-9]: ",
                player, mark);
    }

    // ================================
    // BOT FLOW
    // ================================

    @Override
    public void showBotThinking(String botName, int dotDelayInMS) {
        try {
            output.printf("> [BOT] %s is thinking", botName);

            int dots = 6 + new java.util.Random().nextInt(6);

            for (int i = 0; i < dots; i++) {
                Thread.sleep(dotDelayInMS);
                output.print(".");
            }

            output.println();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Interrupted while bot thinking!");
        }
    }

    @Override
    public void showBotMove(String botName, int blockNo, char mark) {
        output.printf("> [MOVE] %s placed (%c) at position %d%n",
                botName, mark, blockNo + 1);
    }

    // ================================
    // ROUND RESULT
    // ================================

    @Override
    public void showWinner(Player player) {
        output.printf("> [RESULT] %s wins this round%n%n", player);
    }

    @Override
    public void showBotWinner(String botName) {
        output.printf("%n> [RESULT] %s wins this round%n%n", botName);
    }

    @Override
    public void showBotVsBotRoundWinner(String winnerName, String loserName) {
        output.printf("%n> [RESULT] %s defeated %s this round%n%n",
                winnerName, loserName);
    }

    @Override
    public void showTie() {
        output.printf("> [RESULT] Deadlock! Round ended in a draw%n%n");
    }

    // ================================
    // SCORE + MATCH FLOW
    // ================================

    @Override
    public void showScoreboard(String p1name, int w1, String p2name, int w2, int ties) {

        String title = "<< Current Scoreboard >>";

        int nameWidth = Math.max(p1name.length(), p2name.length());

        int scoreWidth = Math.max(
                String.valueOf(w1).length(),
                String.valueOf(w2).length()
        );

        String line1 = String.format("%-" + nameWidth + "s : %" + scoreWidth + "d", p1name, w1);
        String line2 = String.format("%-" + nameWidth + "s : %" + scoreWidth + "d", p2name, w2);

        String tieLine = ties > 0 ? String.format("Tie : %d", ties) : "";

        int maxWidth = Math.max(title.length(), Math.max(line1.length(), line2.length()));
        if (!tieLine.isEmpty()) maxWidth = Math.max(maxWidth, tieLine.length());

        int boxWidth = maxWidth + 2;

        output.println("╔" + "═".repeat(boxWidth) + "╗");

        int leftPad = (boxWidth - title.length()) / 2;
        int rightPad = boxWidth - title.length() - leftPad;
        output.printf("║%" + (leftPad + title.length()) + "s%" + rightPad + "s║%n", title, "");

        output.println("╠" + "═".repeat(boxWidth) + "╣");

        output.printf("║ %-" + maxWidth + "s ║%n", line1);
        output.printf("║ %-" + maxWidth + "s ║%n", line2);

        if (!tieLine.isEmpty()) {
            output.println("╠" + "═".repeat(boxWidth) + "╣");
            output.printf("║ %-" + maxWidth + "s ║%n", tieLine);
        }

        output.println("╚" + "═".repeat(boxWidth) + "╝");

        output.printf("%n> [INPUT] Press ENTER to continue");
    }

    @Override
    public void showNextRoundPrompt() {
        output.printf("%n> [INPUT] Ready for the next round? (Y/N): ");
    }

    // ================================
    // FINAL MATCH RESULT
    // ================================

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void showPlayerWinnerBox(String name) {
        String line1 = "<< MATCH RESULT >>";
        String line2 = "Congratulations, " + name + "!";
        String line3 = "You dominated this match!";

        @SuppressWarnings("DataFlowIssue")
        int innerWidth = Math.max(
                Math.max(
                        line1.length(),
                        line2.length()
                ), line3.length()
        ) + 4;

        int leftPad = (innerWidth - line1.length()) / 2;
        int rightPad = innerWidth - line1.length() - leftPad;

        output.println();
        output.println("╔" + "═".repeat(innerWidth) + "╗");
        output.println("║" + " ".repeat(leftPad) + line1 + " ".repeat(rightPad) + "║");
        output.println("╠" + "═".repeat(innerWidth) + "╣");
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line2);
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line3);
        output.println("╚" + "═".repeat(innerWidth) + "╝");
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void showBotWinnerBox(String name) {
        String line1 = "<< MATCH RESULT >>";
        String line2 = "Match Winner : " + name;
        String line3 = name + " dominated this match!";

        int innerWidth = Math.max(
                Math.max(
                        line1.length(),
                        line2.length()
                ), line3.length()
        ) + 4;

        int leftPad = (innerWidth - line1.length()) / 2;
        int rightPad = innerWidth - line1.length() - leftPad;

        output.println();
        output.println("╔" + "═".repeat(innerWidth) + "╗");
        output.println("║" + " ".repeat(leftPad) + line1 + " ".repeat(rightPad) + "║");
        output.println("╠" + "═".repeat(innerWidth) + "╣");
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line2);
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line3);
        output.println("╚" + "═".repeat(innerWidth) + "╝");
    }

    @Override
    public void showMatchDraw() {
        output.println(Strings.MATCH_DRAW_BOARD);
    }

    // ================================
    // UTILITY
    // ================================

    @Override
    public void prompt(String message) {
        output.print(message);
    }

    @Override
    public void showUndoOffer() {
        output.print("""
                
                > [SETUP] Enable undo for this session?
                > [INFO]  If enabled, wins in this specific game will NOT be updated on Leaderboard.
                
                > [INPUT] Enable undo? (Y/N):\s""");
    }

    @Override
    public void showUndoEnabled() {
        output.print("> [SETUP] Undo enabled — type 'undo' during your move to undo the last turn.\n");
    }

    @Override
    public void showUndoDisabled() {
        output.print("> [SETUP] Undo disabled — wins will be tracked normally.\n");
    }

    @Override
    public void showUndoNotAvailable() {
        output.print("> [UNDO] Nothing to undo — no moves have been made this round.\n");
    }
}