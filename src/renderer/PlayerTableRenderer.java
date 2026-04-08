package renderer;

import player.Player;
import renderer.view.PlayerTableView;
import utility.Strings;

import java.io.PrintStream;
import java.util.List;

public class PlayerTableRenderer implements PlayerTableView {

    private final PrintStream output;

    public PlayerTableRenderer(PrintStream out) {
        this.output = out;
    }

    @Override
    public void showBoard(List<Player> list, String title) {
        if (list == null || list.isEmpty()) {
            output.println(Strings.NO_PLAYERS_LEADERBOARD);
            return;
        }

        // ---- compute max widths ----
        int maxWinsLen = 5;
        int maxNameLen = 5;

        for (Player p : list) {
            int nameLen = p.getName().length();
            if (nameLen > maxNameLen) maxNameLen = nameLen;

            int winsLen = Integer.toString(p.getLifetimeWins()).length();
            if (winsLen > maxWinsLen) maxWinsLen = winsLen;
        }

        // padding
        maxNameLen += 4;
        maxWinsLen += 2;

        int rankWidth = 4;
        int innerWidth = rankWidth + maxNameLen + maxWinsLen + 8;

        // ---- PRINT HEADER ----
        output.println("\n\n╔" + "═".repeat(innerWidth) + "╗");

        int sidePadding = (innerWidth - title.length()) / 2;

        output.println("║" +
                " ".repeat(sidePadding) + title +
                " ".repeat(innerWidth - title.length() - sidePadding) +
                "║");

        output.println("╠" + "═".repeat(innerWidth) + "╣");

        // ---- COLUMN HEADERS ----
        output.printf(
                "║ %-" + rankWidth + "s │ %-" + maxNameLen + "s │ %-" + maxWinsLen + "s ║%n",
                "Rank", "Player", "Wins"
        );

        output.println(
                "╟" +
                        "─".repeat(rankWidth + 2) + "┼" +
                        "─".repeat(maxNameLen + 2) + "┼" +
                        "─".repeat(maxWinsLen + 2) + "╢"
        );

        // ---- ROWS ----
        int rank = 1;
        for (Player p : list) {
            output.printf(
                    "║ %-" + rankWidth + "d │ %-" + maxNameLen + "s │ %-" + maxWinsLen + "d ║%n",
                    rank++, p.getName(), p.getLifetimeWins()
            );
        }

        output.println("╚" + "═".repeat(innerWidth) + "╝\n");
    }
}