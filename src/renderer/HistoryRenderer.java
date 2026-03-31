package renderer;

import engine.GameResult;

import java.io.PrintStream;
import java.util.List;

public class HistoryRenderer {

    private final PrintStream out;

    public HistoryRenderer(PrintStream out) {
        this.out = out;
    }

    public void showEmptyMessage() {

        String message = "📋 No games recorded yet.";

        int innerWidth = message.length() + 6;

        out.println("\n╔" + "═".repeat(innerWidth) + "╗");

        int sidePadding = (innerWidth - message.length()) / 2;

        out.println("║" +
                " ".repeat(sidePadding) + message +
                " ".repeat(innerWidth - message.length() - sidePadding) +
                "║");

        out.println("╚" + "═".repeat(innerWidth) + "╝\n");
    }

    public void showSessions(List<GameResult> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            showEmptyMessage();
            return;
        }

        // ── 1. Compute natural content widths ──────────────────────────────────
        int idxWidth    = Math.max(1, Integer.toString(sessions.size()).length());
        int p1Width     = "Player1".length();
        int p2Width     = "Player2".length();
        int resultWidth = "Result".length();
        int leadWidth   = "Lead".length();

        for (GameResult s : sessions) {
            p1Width     = Math.max(p1Width,     s.getP1Name().length());
            p2Width     = Math.max(p2Width,     s.getP2Name().length());
            resultWidth = Math.max(resultWidth, s.getResult().length());
            leadWidth   = Math.max(leadWidth,
                    Integer.toString(Math.abs(s.getWins1() - s.getWins2())).length());
        }

        // ── 2. Add padding ─────────────────────────────────────────────────────
        final int PAD = 2;
        idxWidth    += PAD;
        p1Width     += PAD;
        p2Width     += PAD;
        resultWidth += PAD;
        leadWidth   += PAD;

        // ── 3. innerWidth ──────────────────────────────────────────────────────
        int innerWidth = idxWidth + p1Width + p2Width + resultWidth + leadWidth + 9;

        // ── 4. Row formats ─────────────────────────────────────────────────────
        // Data rows use │
        String dataRowFmt = "║ %-" + idxWidth    + "s"
                + "│ %-" + p1Width     + "s"
                + "│ %-" + p2Width     + "s"
                + "│ %-" + resultWidth + "s"
                + "│ %-" + leadWidth   + "s║%n";

        // Header row uses ║
        String headRowFmt = "║ %-" + idxWidth    + "s"
                + "║ %-" + p1Width     + "s"
                + "║ %-" + p2Width     + "s"
                + "║ %-" + resultWidth + "s"
                + "║ %-" + leadWidth   + "s║%n";

        // ── 5. Separators ──────────────────────────────────────────────────────
        // After header: ╠═╣  (double-line, matches the ║ dividers in header row)
        String heavySep = "╠"
                + "═".repeat(1 + idxWidth)    + "═"
                + "═".repeat(1 + p1Width)     + "═"
                + "═".repeat(1 + p2Width)     + "═"
                + "═".repeat(1 + resultWidth) + "═"
                + "═".repeat(1 + leadWidth)   + "╣";

        // After each data row: ╟─╢  (light-line)
        String lightSep = "╟"
                + "─".repeat(1 + idxWidth)    + "┼"
                + "─".repeat(1 + p1Width)     + "┼"
                + "─".repeat(1 + p2Width)     + "┼"
                + "─".repeat(1 + resultWidth) + "┼"
                + "─".repeat(1 + leadWidth)   + "╢";

        // ── 6. Title (emoji-aware centering) ──────────────────────────────────
        String title      = " \uD83D\uDCDC GAME HISTORY \uD83D\uDCDC ";
        int    emojiCount = 2;
        int    displayLen = title.length() - emojiCount;
        int    leftPad    = (innerWidth - displayLen) / 2 - 1;
        int    rightPad   = innerWidth - displayLen - leftPad - 2;

        // ── 7. Print ───────────────────────────────────────────────────────────
        out.println();
        out.println("╔" + "═".repeat(innerWidth) + "╗");
        out.println("║" + " ".repeat(leftPad) + title + " ".repeat(rightPad) + "║");
        out.println("╠" + "═".repeat(innerWidth) + "╣");

        // Header row with ║ dividers, followed by heavy ╬ separator
        out.printf(headRowFmt, "#", "Player1", "Player2", "Result", "Lead");
        out.println(heavySep);

        // Data rows, each followed by a light ┼ separator
        for (int i = 0; i < sessions.size(); i++) {
            GameResult s    = sessions.get(i);
            int         lead = Math.abs(s.getWins1() - s.getWins2());
            out.printf(dataRowFmt,
                    i + 1,
                    s.getP1Name(),
                    s.getP2Name(),
                    s.getResult(),
                    lead);

            // Print light separator after every row including the last,
            // then overwrite the last one with the bottom border
            if (i < sessions.size() - 1) {
                out.println(lightSep);
            }
        }

        out.println("╚" + "═".repeat(innerWidth) + "╝");
        out.println();
    }

}