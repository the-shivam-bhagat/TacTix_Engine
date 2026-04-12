package renderer.classes;

import core.GameResult;
import renderer.view.HistoryView;

import java.io.PrintStream;
import java.util.List;

public class HistoryRenderer implements HistoryView {

    private final PrintStream out;

    public HistoryRenderer(PrintStream out) {
        this.out = out;
    }

    public void showEmptyMessage() {

        String message = "[INFO] No game history available";

        int innerWidth = message.length() + 6;

        out.println("\n╔" + "═".repeat(innerWidth) + "╗");

        int sidePadding = (innerWidth - message.length()) / 2;

        out.println("║" +
                " ".repeat(sidePadding) + message +
                " ".repeat(innerWidth - message.length() - sidePadding) +
                "║");

        out.println("╚" + "═".repeat(innerWidth) + "╝\n");
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void showSessions(List<GameResult> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            showEmptyMessage();
            return;
        }

        // ── 1. Compute natural content widths ──────────────────────────────────
        int idxWidth = Math.max(1, Integer.toString(sessions.size()).length());
        int p1Width = "P1".length();
        int p2Width = "P2".length();
        int resultWidth = "RESULT".length();
        int leadWidth = "LEAD".length();
        int roundsWidth = "ROUNDS".length();

        for (GameResult s : sessions) {
            p1Width = Math.max(p1Width, s.getP1Name().length());
            p2Width = Math.max(p2Width, s.getP2Name().length());
            resultWidth = Math.max(resultWidth, s.getResult().length());
            leadWidth = Math.max(leadWidth,
                    Integer.toString(Math.abs(s.getWins1() - s.getWins2())).length());
            roundsWidth = Math.max(roundsWidth,
                    Integer.toString(s.getTotalRounds()).length());
        }

        // ── 2. Add padding ─────────────────────────────────────────────────────
        final int PAD = 2;
        idxWidth += PAD;
        p1Width += PAD;
        p2Width += PAD;
        resultWidth += PAD;
        leadWidth += PAD;
        roundsWidth += PAD;

        // ── 3. innerWidth ──────────────────────────────────────────────────────
        int innerWidth = idxWidth + p1Width + p2Width + resultWidth + leadWidth + roundsWidth + 11;

        // ── 4. Row formats ─────────────────────────────────────────────────────
        String dataRowFmt = "║ %-" + idxWidth + "s"
                + "│ %-" + p1Width + "s"
                + "│ %-" + p2Width + "s"
                + "│ %-" + resultWidth + "s"
                + "│ %-" + leadWidth + "s"
                + "│ %-" + roundsWidth + "s║%n";

        String headRowFmt = "║ %-" + idxWidth + "s"
                + "║ %-" + p1Width + "s"
                + "║ %-" + p2Width + "s"
                + "║ %-" + resultWidth + "s"
                + "║ %-" + leadWidth + "s"
                + "║ %-" + roundsWidth + "s║%n";

        // ── 5. Separators ──────────────────────────────────────────────────────
        String heavySep = "╠"
                + "═".repeat(1 + idxWidth) + "═"
                + "═".repeat(1 + p1Width) + "═"
                + "═".repeat(1 + p2Width) + "═"
                + "═".repeat(1 + resultWidth) + "═"
                + "═".repeat(1 + leadWidth) + "═"
                + "═".repeat(1 + roundsWidth) + "╣";

        String lightSep = "╟"
                + "─".repeat(1 + idxWidth) + "┼"
                + "─".repeat(1 + p1Width) + "┼"
                + "─".repeat(1 + p2Width) + "┼"
                + "─".repeat(1 + resultWidth) + "┼"
                + "─".repeat(1 + leadWidth) + "┼"
                + "─".repeat(1 + roundsWidth) + "╢";

        // ── 6. Title centering — simple, no emoji offset ──────────────────────
        String title = " [HISTORY] GAME SESSIONS ";
        int leftPad = (innerWidth - title.length()) / 2;
        int rightPad = innerWidth - title.length() - leftPad;

        // ── 7. Print ───────────────────────────────────────────────────────────
        out.println();
        out.println("╔" + "═".repeat(innerWidth) + "╗");
        out.println("║" + " ".repeat(leftPad) + title + " ".repeat(rightPad) + "║");
        out.println("╠" + "═".repeat(innerWidth) + "╣");

        out.printf(headRowFmt, "#", "P1", "P2", "RESULT", "LEAD", "ROUNDS");
        out.println(heavySep);

        for (int i = 0; i < sessions.size(); i++) {
            GameResult s = sessions.get(i);
            int lead = Math.abs(s.getWins1() - s.getWins2());
            out.printf(dataRowFmt,
                    i + 1,
                    s.getP1Name(),
                    s.getP2Name(),
                    s.getResult(),
                    lead,
                    s.getTotalRounds());

            if (i < sessions.size() - 1) {
                out.println(lightSep);
            }
        }

        out.println("╚" + "═".repeat(innerWidth) + "╝");
        out.println();
    }

}