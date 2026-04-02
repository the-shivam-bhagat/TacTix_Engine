package bot;

import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotNames.UNBEATABLE_BOT_NAME;

// PROBE  Priority-Reordered Opening-Boosted EquiMinMax

// This is philosophically distinct from randomized alpha-beta
// (which randomizes search order inside the tree for pruning efficiency)
//  — this randomizes the output among provably equivalent choices
// for variety without sacrificing correctness.

// I can honestly say:
// "I implemented minimax + alpha-beta pruning with priority move ordering,
// an opening book, and equimax output selection —
// the specific combination and architecture is my own design."


/// Perfect play -- Full Minimax
public class UnbeatableBot implements Bot {
    private static final String MODE = "UNBEATABLE_BOT";
    private final String name;

    private final Random random;

    public UnbeatableBot(boolean secondInstance) {
        name = UNBEATABLE_BOT_NAME.concat(secondInstance ? "2.0" : "")
                .concat(String.format(" (%s)", MODE));
        random = new Random();
    }

    // bot is always max player
    @Override
    public int chooseMove(int[] board, int botFlag, int step) {
        int opening = getOpeningStrategyMove(board, step, random);
        if (opening != -1) return opening;

        int[] bestChoices = new int[9];
        int idx = 0;

        int bestScore = Integer.MIN_VALUE;

        for (int move : priorityOrder) {
            if (board[move] != 0) continue;

            board[move] = botFlag;

            int score = minMax(board, false, botFlag, 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);

            board[move] = 0;

            if (score >= bestScore) {
                if (score > bestScore) {
                    bestScore = score;
                    idx = 0;
                }
                bestChoices[idx++] = move;
            }
        }

        return bestChoices[random.nextInt(idx)];
    }

    /// here alpha - beta is just min - max helping for pruning choices where player already lost (i.e. worst play)
    private int minMax(int[] board, boolean isBotTurn, int botFlag, int depth, int alpha, int beta) {
        int winner = winnerCheck(board);
        if (winner != 0)
            return winner == botFlag ? (10 - depth) : (depth - 10);

        boolean hasEmpty = false;
        for (int cell : board)
            if (cell == 0) {
                hasEmpty = true;
                break;
            }
        if (!hasEmpty) return 0;

        if (isBotTurn) {
            /* maximizing output */
            int best = Integer.MIN_VALUE;

            for (int move : priorityOrder) {
                if (board[move] != 0) continue;

                board[move] = botFlag;
                int score = minMax(board, false, botFlag, depth + 1, alpha, beta);
                board[move] = 0;

                if (score > best) best = score;
                if (best > alpha) alpha = best;
                if (alpha >= beta) break;
            }

            return best == Integer.MIN_VALUE ? 0 : best;
        } else {
            /* minimizing output */
            int best = Integer.MAX_VALUE;
            int playerFlag = -botFlag;

            for (int move : priorityOrder) {
                if (board[move] != 0) continue;

                board[move] = playerFlag;
                int score = minMax(board, true, botFlag, depth + 1, alpha, beta);
                board[move] = 0;

                if (score < best) best = score;
                if (best < beta) beta = best;
                if (alpha >= beta) break;
            }

            return best == Integer.MAX_VALUE ? 0 : best;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMode() {
        return MODE;
    }
}