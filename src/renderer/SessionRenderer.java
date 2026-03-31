package renderer;

import player.Player;
import utility.Strings;
import utility.Utility;

import java.io.PrintStream;

public class SessionRenderer {

    private final PrintStream output;

    public SessionRenderer(PrintStream output) {
        this.output = output;
    }

    public void showRoundStart(int round) {
        output.printf("%n🚀 Round %d is about to begin! (Press ENTER to continue) ", round);
    }

    public void showFirstMovePrompt(Player first, Player second) {
        output.printf("%n⚔️ %s vs %s!%n%s, want to make the first move? (Y/N): ",
                first, second, first);
    }

    public void showBoard(char[][] board) {
        output.println("\n🎮 Here's the Play Board:");
        Utility.displayPlayBoard(board);
    }

    public void showMovePrompt(Player player, char mark) {
        output.printf("⚔️ %s, make your move (%c) — pick a position (1-9): ",
                player, mark);
    }

    public void showWinner(Player player) {
        output.printf("%n🏆 Congratulations, %s! You WON this round! 🎉%n%n", player);
    }

    public void showTie() {
        output.println("\n💥 Deadlock! Neither side claims victory this round! ⚔️\n");
    }

    public void showScoreboard(Player p1, int w1, Player p2, int w2, int ties) {
        output.printf("""
                
                📊 Current Scoreboard
                ─────────────────────
                %s : %d
                %s : %d%s
                
                (Press ENTER to continue)""",
                p1.getName(), w1,
                p2.getName(), w2,
                ties > 0 ? String.format("%nTie : %d", ties) : ""
        );
    }

    public void showNextRoundPrompt() {
        output.print("\n\n🚀 Ready for the next round? (Y/N): ");
    }

    public void showMatchDraw() {
        output.println(Strings.MATCH_DRAW_BOARD);
    }

    public void showMatchWinnerBox(String name) {
        String line1 = "🏆 MATCH WINNER!";
        String line2 = "Congratulations, " + name + "! 🎉";
        String line3 = "You dominated this match! 💪";

        int innerWidth = Math.max(line2.length(), line3.length()) + 4;

        output.println();
        output.println("╔" + "═".repeat(innerWidth) + "╗");
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line1);
        output.println("╠" + "═".repeat(innerWidth) + "╣");
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line2);
        output.printf("║ %-" + (innerWidth - 2) + "s ║%n", line3);
        output.println("╚" + "═".repeat(innerWidth) + "╝");
    }
}