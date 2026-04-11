package renderer.classes;

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

    // LEADERBOARD
    @Override
    public void showLeaderboard(List<Player> list, String title) {
        renderTable(list, title, false);
    }

    // ADMIN TABLE
    @Override
    public void showAdminTable(List<Player> list, String title) {
        renderTable(list, title, true);
    }

    // ================================================================
    // SHARED MAIN TABLE RENDERER
    // ================================================================

    private void renderTable(List<Player> list, String title, boolean adminMode) {

        if (list == null || list.isEmpty()) {
            output.println(Strings.NO_PLAYERS_LEADERBOARD);
            return;
        }

        int rankWidth = 4;
        int maxNameLen = 6;
        int maxWinsLen = 4;
        int maxLastActive = 11;
        int maxDaysOld = 8;
        int maxMemberSince = 12;

        for (Player p : list) {
            maxNameLen = Math.max(maxNameLen, p.getName().length());
            maxWinsLen = Math.max(maxWinsLen,
                    Integer.toString(p.getLifetimeWins()).length());
            maxLastActive = Math.max(maxLastActive,
                    p.lastActiveDisplay().length());
            maxDaysOld = Math.max(maxDaysOld,
                    Integer.toString(p.daysOld()).length());

            if (adminMode) {
                maxMemberSince = Math.max(maxMemberSince,
                        p.memberSince().length());
            }
        }

        maxNameLen += 2;
        maxWinsLen += 2;
        maxLastActive += 2;
        maxDaysOld += 2;
        maxMemberSince += 2;

        int innerWidth;

        if (adminMode) {
            innerWidth = rankWidth + maxNameLen + maxWinsLen
                    + maxLastActive + maxDaysOld
                    + maxMemberSince + 17;
        } else {
            innerWidth = rankWidth + maxNameLen + maxWinsLen
                    + maxLastActive + maxDaysOld + 14;
        }

        printTitle(title, innerWidth);

        if (adminMode) {
            printAdminHeader(rankWidth, maxNameLen, maxWinsLen,
                    maxLastActive, maxDaysOld, maxMemberSince);

            int rank = 1;

            for (Player p : list) {
                output.printf(
                        "║ %-" + rankWidth + "d │ %-" + maxNameLen + "s │ %-"
                                + maxWinsLen + "d │ %-" + maxLastActive + "s │ %-"
                                + maxDaysOld + "d │ %-" + maxMemberSince + "s ║%n",

                        rank++,
                        p.getName(),
                        p.getLifetimeWins(),
                        p.lastActiveDisplay(),
                        p.daysOld(),
                        p.memberSince()
                );
            }

        } else {

            printLeaderboardHeader(rankWidth, maxNameLen, maxWinsLen,
                    maxLastActive, maxDaysOld);

            int rank = 1;

            for (Player p : list) {
                output.printf(
                        "║ %-" + rankWidth + "d │ %-" + maxNameLen + "s │ %-"
                                + maxWinsLen + "d │ %-" + maxLastActive + "s │ %-"
                                + maxDaysOld + "d ║%n",

                        rank++,
                        p.getName(),
                        p.getLifetimeWins(),
                        p.lastActiveDisplay(),
                        p.daysOld()
                );
            }
        }

        output.println("╚" + "═".repeat(innerWidth) + "╝\n");
    }

    // ================================================================
    // HEADERS
    // ================================================================

    private void printLeaderboardHeader(int rankWidth,
                                        int maxNameLen,
                                        int maxWinsLen,
                                        int maxLastActive,
                                        int maxDaysOld) {

        output.printf(
                "║ %-" + rankWidth + "s │ %-" + maxNameLen + "s │ %-"
                        + maxWinsLen + "s │ %-" + maxLastActive + "s │ %-"
                        + maxDaysOld + "s ║%n",

                "Rank",
                "Player",
                "Wins",
                "Last Active",
                "Days Old"
        );

        output.println(
                "╟" + "─".repeat(rankWidth + 2) + "┼"
                        + "─".repeat(maxNameLen + 2) + "┼"
                        + "─".repeat(maxWinsLen + 2) + "┼"
                        + "─".repeat(maxLastActive + 2) + "┼"
                        + "─".repeat(maxDaysOld + 2) + "╢"
        );
    }

    private void printAdminHeader(int rankWidth,
                                  int maxNameLen,
                                  int maxWinsLen,
                                  int maxLastActive,
                                  int maxDaysOld,
                                  int maxMemberSince) {

        output.printf(
                "║ %-" + rankWidth + "s │ %-" + maxNameLen + "s │ %-"
                        + maxWinsLen + "s │ %-" + maxLastActive + "s │ %-"
                        + maxDaysOld + "s │ %-" + maxMemberSince + "s ║%n",

                "Rank",
                "Player",
                "Wins",
                "Last Active",
                "Days Old",
                "Member Since"
        );

        output.println(
                "╟" + "─".repeat(rankWidth + 2) + "┼"
                        + "─".repeat(maxNameLen + 2) + "┼"
                        + "─".repeat(maxWinsLen + 2) + "┼"
                        + "─".repeat(maxLastActive + 2) + "┼"
                        + "─".repeat(maxDaysOld + 2) + "┼"
                        + "─".repeat(maxMemberSince + 2) + "╢"
        );
    }

    // ================================================================
    // TITLE
    // ================================================================

    private void printTitle(String title, int innerWidth) {

        output.println("\n\n╔" + "═".repeat(innerWidth) + "╗");

        int sidePadding = (innerWidth - title.length()) / 2;

        output.println(
                "║"
                        + " ".repeat(sidePadding)
                        + title
                        + " ".repeat(innerWidth - title.length() - sidePadding)
                        + "║"
        );

        output.println("╠" + "═".repeat(innerWidth) + "╣");
    }
}