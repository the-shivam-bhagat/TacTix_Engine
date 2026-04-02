package renderer;

import bot.Bot;
import player.Player;
import renderer.view.SessionView;
import utility.Logger;
import utility.Strings;
import utility.Utility;

import java.io.PrintStream;

public class SessionRenderer implements SessionView {

    private final PrintStream output;

    public SessionRenderer(PrintStream output) {
        this.output = output;
    }

    @Override
    public void showRoundStart(int round) {
        output.printf("%n🚀 Round %d is about to begin! (Press ENTER to continue) ", round);
    }

    @Override
    public void showFirstMovePrompt(Player first, Player second) {
        output.printf("%n⚔️ %s vs %s!%n%s, want to make the first move? (Y/N): ",
                first, second, first);
    }

    @Override
    public void showBoard(char[][] board) {
        output.println("\n🎮 Here's the Play Board:");
        Utility.displayPlayBoard(board);
    }

    @Override
    public void showMovePrompt(Player player, char mark) {
        output.printf("⚔️ %s, make your move (%c) — pick a position (1-9): ",
                player, mark);
    }

    @Override
    public void showWinner(Player player) {
        output.printf("%n🏆 Congratulations, %s! You WON this round! 🎉%n%n", player);
    }

    @Override
    public void showTie() {
        output.println("\n💥 Deadlock! Neither side claims victory this round! ⚔️\n");
    }

    @Override
    public void showScoreboard(String p1name, int w1, String p2name, int w2, int ties) {

        String title = "📊 Current Scoreboard";

        // Width for names
        int nameWidth = Math.max(p1name.length(), p2name.length());

        // Width for scores (handles multi-digit safely)
        int scoreWidth = Math.max(
                String.valueOf(w1).length(),
                String.valueOf(w2).length()
        );

        // Build formatted player lines (aligned)
        String line1 = String.format("%-" + nameWidth + "s : %" + scoreWidth + "d", p1name, w1);
        String line2 = String.format("%-" + nameWidth + "s : %" + scoreWidth + "d", p2name, w2);

        String tieLine = ties > 0 ? String.format("Tie : %d", ties) : "";

        // Calculate max width
        int maxWidth = Math.max(title.length(), Math.max(line1.length(), line2.length()));
        if (!tieLine.isEmpty()) maxWidth = Math.max(maxWidth, tieLine.length());

        int boxWidth = maxWidth + 2; // padding inside borders

        // Top
        output.println();
        output.println("┌" + "─".repeat(boxWidth) + "┐");

        // Title (centered)
        int leftPad = (boxWidth - title.length()) / 2;
        int rightPad = boxWidth - title.length() - leftPad;
        output.printf("│%" + (leftPad + title.length()) + "s%" + rightPad + "s│%n", title, "");

        // Divider
        output.println("├" + "─".repeat(boxWidth) + "┤");

        // Player rows (perfect alignment)
        output.printf("│ %-" + maxWidth + "s │%n", line1);
        output.printf("│ %-" + maxWidth + "s │%n", line2);

        // Tie
        if (!tieLine.isEmpty()) {
            output.println("├" + "─".repeat(boxWidth) + "┤");
            output.printf("│ %-" + maxWidth + "s │%n", tieLine);
        }

        // Bottom
        output.println("└" + "─".repeat(boxWidth) + "┘");

        output.print("\n(Press ENTER to continue)");
    }

    @Override
    public void showNextRoundPrompt() {
        output.print("\n\n🚀 Ready for the next round? (Y/N): ");
    }

    @Override
    public void showMatchDraw() {
        output.println(Strings.MATCH_DRAW_BOARD);
    }

    @Override
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

    @Override
    public void prompt(String message) {
        output.print(message);
    }

    @Override
    public void showBotThinking(Bot bot) {
        try {
            String botLabel = String.format("🤖 %s Bot (%s)",
                    bot.getMode(), bot.getName());

            output.print(botLabel + " is thinking");

            int dots = 6 + new java.util.Random().nextInt(5);

            for (int i = 0; i < dots; i++) {
                Thread.sleep(200);
                output.print(".");
            }

            output.println();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Interrupted while bot thinking!");
        }
    }

    @Override
    public void showBotMove(Bot bot, int blockNo, char mark) {
        output.printf("→ %s Bot (%s) places (%c) at position %d%n",
                bot.getMode(), bot.getName(), mark, blockNo + 1);
    }

    @Override
    public void showBotWinner(Bot bot) {
        output.printf("%n🤖 %s Bot (%s) wins this round! 💥%n%n",
                bot.getMode(), bot.getName());
    }

    @Override
    public void showFirstMovePrompt(Player player, Bot bot) {
        output.printf("%n⚔️ %s vs %s Bot (%s)!%n%s, want to make the first move? (Y/N): ",
                player.getName(),
                bot.getMode(),
                bot.getName(),
                player.getName());
    }
}