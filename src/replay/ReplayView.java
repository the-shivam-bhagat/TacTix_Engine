package replay;

import core.GameResult;

public interface ReplayView {

    void showReplayOffer();

    void showSelectGamePrompt(int max);

    void showSelectRoundPrompt(int max);

    void showReplayAnotherRound();

    void showReplayAnotherMatch();

    void showNoRoundsAvailable();

    void showRoundAbandoned(int roundNo);

    void showMatchSummaryBox(GameResult result, int gameNo);

    void showRoundTable(GameResult result);

    void showReplayHeader(int gameNo, int roundNo, String firstPlayer);

    void showReplayStep(int stepNo, String playerName, char mark, int cell);

    void showReplayBoard(int[] freq, int lastMove);

    void showReplayContinuePrompt();

    void showReplayRoundComplete(String winner);
}