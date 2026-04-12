package core;

import java.util.List;

public final class GameResult {

    private final String p1Name;
    private final String p2Name;
    private final int wins1;
    private final int wins2;
    private final String result;

    // Per-round data — all three lists are parallel, same index = same round.
    // null entries = that round was abandoned mid-play via end command.
    // Total rounds played = this list's size = wins1 + wins2 + ties + abandoned.

    /// Ordered cell indexes (0–8) played in that round. null = abandoned.
    private final List<List<Integer>> roundMoves;

    /// true = first listed player (p1/player/bot1) went first that round.
    /// Allows replay to assign flag 1 to correct player.
    private final List<String> roundFirstPlayerStarts;

    /// Winner name, "TIE", or null if abandoned.
    private final List<String> roundWinners;

    public GameResult(String p1Name, String p2Name,
                      int wins1, int wins2, String result,
                      List<List<Integer>> roundMoves,
                      List<String> roundFirstPlayerStarts,
                      List<String> roundWinners) {
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.wins1 = wins1;
        this.wins2 = wins2;
        this.result = result;
        this.roundMoves = roundMoves;
        this.roundFirstPlayerStarts = roundFirstPlayerStarts;
        this.roundWinners = roundWinners;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }

    public int getWins1() {
        return wins1;
    }

    public int getWins2() {
        return wins2;
    }

    public String getResult() {
        return result;
    }

    public List<List<Integer>> getRoundMoves() {
        return roundMoves;
    }

    public List<String> getRoundFirstPlayerStarts() {
        return roundFirstPlayerStarts;
    }

    public List<String> getRoundWinners() {
        return roundWinners;
    }

    /// Total rounds including abandoned — size of any of the three parallel lists
    public int getTotalRounds() {
        return roundMoves.size();
    }

    @Override
    public String toString() {
        return "GameResult{p1='" + p1Name + "', p2='" + p2Name + '\'' +
                ", wins1=" + wins1 + ", wins2=" + wins2 +
                ", result='" + result + "', rounds=" + getTotalRounds() + "}";
    }
}