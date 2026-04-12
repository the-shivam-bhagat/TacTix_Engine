package replay;

import core.GameResult;

import java.io.PrintStream;
import java.util.List;

public class ReplayRenderer implements ReplayView {

    private final PrintStream out;

    public ReplayRenderer(PrintStream out) {
        this.out = out;
    }

    @Override
    public void showReplayOffer() {
        out.print("> [INPUT] Do you want to replay matches? (Y/N): ");
    }

    @Override
    public void showSelectGamePrompt(int max) {
        out.printf("%n> [INFO]  Choose a game number to replay any round.%n" +
                "%n> [INPUT] Enter game number (1-%d): ", max);
    }

    @Override
    public void showSelectRoundPrompt(int max) {
        out.printf("%n> [INPUT] Enter round number to replay (1-%d): ", max);
    }

    @Override
    public void showReplayAnotherRound() {
        out.print("\n> [INPUT] Replay another round from this match? (Y/N): ");
    }

    @Override
    public void showReplayAnotherMatch() {
        out.print("\n> [INPUT] Replay a round from another match? (Y/N): ");
    }

    @Override
    public void showNoRoundsAvailable() {
        out.print("\n> [INFO] No rounds available to replay for this match.\n");
    }

    @Override
    public void showRoundAbandoned(int roundNo) {
        out.printf("%n> [INFO] Round %d was abandoned — no moves to replay.%n", roundNo);
    }

    // ── Match summary box ──────────────────────────────────────────────────

    @Override
    public void showMatchSummaryBox(GameResult result, int gameNo) {
        String title    = String.format("GAME %d — MATCH SUMMARY", gameNo);
        String p1line   = String.format("%-12s : %d win(s)", result.getP1Name(), result.getWins1());
        String p2line   = String.format("%-12s : %d win(s)", result.getP2Name(), result.getWins2());
        String resLine  = "Result  : " + result.getResult();
        String rndLine  = "Rounds  : " + result.getTotalRounds();

        int w = Math.max(title.length(),
                Math.max(p1line.length(),
                        Math.max(p2line.length(),
                                Math.max(resLine.length(), rndLine.length())))) + 4;

        out.println();
        out.println("╔" + "═".repeat(w) + "╗");
        int pad = (w - title.length()) / 2;
        out.println("║" + " ".repeat(pad) + title
                + " ".repeat(w - title.length() - pad) + "║");
        out.println("╠" + "═".repeat(w) + "╣");
        out.printf("║ %-" + (w - 2) + "s ║%n", p1line);
        out.printf("║ %-" + (w - 2) + "s ║%n", p2line);
        out.println("╟" + "─".repeat(w) + "╢");
        out.printf("║ %-" + (w - 2) + "s ║%n", resLine);
        out.printf("║ %-" + (w - 2) + "s ║%n", rndLine);
        out.println("╚" + "═".repeat(w) + "╝");
    }

    // ── Round table ────────────────────────────────────────────────────────

    @SuppressWarnings({"DuplicateExpressions", "DuplicatedCode"})
    @Override
    public void showRoundTable(GameResult result) {
        List<String> winners = result.getRoundWinners();
        List<List<Integer>> moves = result.getRoundMoves();

        int rndWidth   = "Round No".length();
        int resWidth   = "Result".length();
        int stepsWidth = "StepCount".length();

        for (int i = 0; i < winners.size(); i++) {
            String w   = winners.get(i) != null ? winners.get(i) : "Abandoned";
            resWidth   = Math.max(resWidth,   w.length());
            List<Integer> m = moves.get(i);
            stepsWidth = Math.max(stepsWidth,
                    Integer.toString(m != null ? m.size() : 0).length());
        }

        // padding
        rndWidth   += 2;
        resWidth   += 2;
        stepsWidth += 2;

        int innerWidth = rndWidth + resWidth + stepsWidth + 8;

        // title
        String title   = " ROUNDS ";
        int    lp      = (innerWidth - title.length()) / 2;
        int    rp      = innerWidth - title.length() - lp;

        out.println();
        out.println("╔" + "═".repeat(innerWidth) + "╗");
        out.println("║" + " ".repeat(lp) + title + " ".repeat(rp) + "║");
        out.println("╠" + "═".repeat(innerWidth) + "╣");

        // header row
        out.printf("║ %-" + rndWidth   + "s │ %-"
                        + resWidth   + "s │ %-"
                        + stepsWidth + "s ║%n",
                "Round No", "Result", "StepCount");

        // header separator
        out.println("╟" + "─".repeat(rndWidth + 2)   + "┼"
                + "─".repeat(resWidth + 2)    + "┼"
                + "─".repeat(stepsWidth + 2)  + "╢");

        // data rows
        for (int i = 0; i < winners.size(); i++) {
            String       w     = winners.get(i) != null ? winners.get(i) : "Abandoned";
            List<Integer> m    = moves.get(i);
            int          steps = m != null ? m.size() : 0;

            out.printf("║ %-" + rndWidth   + "d │ %-"
                            + resWidth   + "s │ %-"
                            + stepsWidth + "d ║%n",
                    i + 1, w, steps);

            if (i < winners.size() - 1) {
                out.println("╟" + "─".repeat(rndWidth + 2)   + "┼"
                        + "─".repeat(resWidth + 2)    + "┼"
                        + "─".repeat(stepsWidth + 2)  + "╢");
            }
        }

        out.println("╚" + "═".repeat(innerWidth) + "╝");
    }

    @Override
    public void showReplayBoard(int[] freq, int lastMove) {
        // Inner cell width = 5 chars ("  X  " or " [X] ")
        // Box outer: ╔═══════════════════════╗
        //            ║  ┌─────┬─────┬─────┐  ║
        //            ║  │  X  │ [O] │     │  ║
        //            ║  ├─────┼─────┼─────┤  ║
        //            ║  └─────┴─────┴─────┘  ║
        //            ╚═══════════════════════╝

        int cellW   = 5;   // " " + content(3) + " " inside │
        int gridW   = cellW * 3 + 4; // three cells + 4 separators (│)
        int outerW  = gridW + 4;     // 2 spaces indent each side inside ║

        out.println();
        out.println("  ╔" + "═".repeat(outerW) + "╗");
        out.println("  ║  ┌" + ("─".repeat(cellW) + "┬").repeat(2)
                + "─".repeat(cellW) + "┐  ║");

        for (int row = 0; row < 3; row++) {
            StringBuilder line = new StringBuilder("  ║  │");

            for (int col = 0; col < 3; col++) {
                int  idx = row * 3 + col;
                char c   = freq[idx] == 1 ? 'X' : freq[idx] == -1 ? 'O' : ' ';

                if (idx == lastMove)
                    line.append(String.format(" [%c] ", c));
                else
                    line.append(String.format("  %c  ", c));

                line.append("│");
            }

            line.append("  ║");
            out.println(line);

            if (row < 2) {
                out.println("  ║  ├" + ("─".repeat(cellW) + "┼").repeat(2)
                        + "─".repeat(cellW) + "┤  ║");
            }
        }

        out.println("  ║  └" + ("─".repeat(cellW) + "┴").repeat(2)
                + "─".repeat(cellW) + "┘  ║");
        out.println("  ╚" + "═".repeat(outerW) + "╝");
        out.println();
    }

    // ── Replay step display ────────────────────────────────────────────────

    @Override
    public void showReplayHeader(int gameNo, int roundNo, String firstPlayer) {
        out.printf("""
                
                > [REPLAY] Game %d — Round %d
                > [REPLAY] First player: %s
                > [REPLAY] X = first player | O = second player
                > [INPUT]  Press ENTER to step through moves
                """, gameNo, roundNo, firstPlayer);
    }

    @Override
    public void showReplayStep(int stepNo, String playerName, char mark, int cell) {
        out.printf("%n> [REPLAY] Step %d — %s (%c) plays cell %d%n",
                stepNo + 1, playerName, mark, cell + 1);
    }

    @Override
    public void showReplayContinuePrompt() {
        out.print("> [INPUT] Press ENTER to continue: ");
    }

    @Override
    public void showReplayRoundComplete(String winner) {
        if ("TIE".equals(winner))
            out.print("\n> [REPLAY] Round ended in a draw.\n");
        else
            out.printf("%n> [REPLAY] Round winner: %s%n", winner);
    }
}