package replay;

import core.GameResult;
import input.Input;
import utility.Logger;

import java.util.List;

/// Drives the full replay flow — match selection, round selection, step-through.
/// Called from GameHistory after the session table is displayed.
public final class ReplayEngine {

    private final Input input;
    private final ReplayView renderer;

    public ReplayEngine(Input input, ReplayView renderer) {
        this.input = input;
        this.renderer = renderer;
    }

    /// Entry point — called after history table is shown.
    public void offerReplay(List<GameResult> sessions) {
        if (sessions == null || sessions.isEmpty()) return;

        renderer.showReplayOffer();
        if (!input.readYesNo_Specific()) return;

        // Outer loop — choose a different match
        boolean replayAnotherMatch = true;
        while (replayAnotherMatch) {
            renderer.showSelectGamePrompt(sessions.size());
            int gameNo = input.readBoundedInt(1, sessions.size());

            GameResult chosen = sessions.get(gameNo - 1);

            renderer.showMatchSummaryBox(chosen, gameNo);
            renderer.showRoundTable(chosen);

            // Check if there are any replayable rounds
            boolean hasReplayable = hasReplayableRounds(chosen);

            if (!hasReplayable) {
                renderer.showNoRoundsAvailable();
            } else {
                // Inner loop — replay rounds within this match
                boolean replayAnotherRound = true;
                while (replayAnotherRound) {
                    renderer.showSelectRoundPrompt(chosen.getTotalRounds());
                    int roundNo = input.readBoundedInt(1, chosen.getTotalRounds());

                    replayRound(chosen, gameNo, roundNo);

                    renderer.showReplayAnotherRound();
                    replayAnotherRound = input.readYesNo_Specific();
                }
            }

            renderer.showReplayAnotherMatch();
            replayAnotherMatch = input.readYesNo_Specific();
        }
    }

    private boolean hasReplayableRounds(GameResult chosen) {
        for (List<Integer> moves : chosen.getRoundMoves())
            if (moves != null && !moves.isEmpty()) return true;
        return false;
        // return chosen.getRoundMoves().stream().anyMatch(m -> m != null && !m.isEmpty());
    }

    /// Replays a single round step-by-step.
    private void replayRound(GameResult result, int gameNo, int roundNo) {
        int idx = roundNo - 1;

        List<Integer> moves = result.getRoundMoves().get(idx);
        String firstPlayer = result.getRoundFirstPlayerStarts().get(idx);
        String winner = result.getRoundWinners().get(idx);

        // Abandoned round — no moves
        if (moves == null) {
            renderer.showRoundAbandoned(roundNo);
            return;
        }

        renderer.showReplayHeader(gameNo, roundNo, firstPlayer);

        // Determine the two player names and marks from the result
        String p1Name = result.getP1Name(); // went first = flag 1 = X
        String p2Name = result.getP2Name();

        // firstPlayer name tells us who had flag 1 (X) this round
        // If firstPlayer == p1Name → p1 is X, p2 is O
        // If firstPlayer == p2Name → p2 is X, p1 is O
        boolean firstIsP1 = firstPlayer.equals(p1Name);

        String xPlayer = firstIsP1 ? p1Name : p2Name;
        String oPlayer = firstIsP1 ? p2Name : p1Name;

        int[] freq = new int[9]; // tracks board state during replay

        for (int step = 0; step < moves.size(); step++) {
            int cell = moves.get(step);
            int flag = (step % 2 == 0) ? 1 : -1;  // even steps = first player
            char mark = (step % 2 == 0) ? 'X' : 'O';
            String name = (step % 2 == 0) ? xPlayer : oPlayer;

            freq[cell] = flag;

            renderer.showReplayStep(step, name, mark, cell);
            renderer.showReplayBoard(freq, cell);
            renderer.showReplayContinuePrompt();

            input.waitForEnter(); // each move requires enter — lets user pace themselves
        }

        // Round complete — show result
        renderer.showReplayRoundComplete(winner != null ? winner : "Abandoned");
        Logger.info(String.format("Replay complete — Game %d Round %d", gameNo, roundNo));
    }
}