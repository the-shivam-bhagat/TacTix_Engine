package bot;

import java.util.Random;

import static bot.UtilBot.*;

public class StallBot implements Bot {

    private static final String MODE = "STALL_BOT";
    private static final String ELO_RATING = "??";

    private final String name;
    private final Random random = new Random();

    public StallBot() {
        this.name = "STALL";
    }

    public StallBot(boolean firstInstance) {
        this.name = "STALL".concat(firstInstance ? "-α" : "-β");
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int stepNo) {

        // ================================
        // 🔥 OPENING RULES (KEEP THESE)
        // ================================

        if (stepNo == 0) return 4;

        if (stepNo == 1 && board[4] == -botFlag) {
            for (int c : corners) {
                if (board[c] == 0) return c;
            }
        }

        if (stepNo == 1 && board[4] == botFlag) {
            for (int c : corners) {
                if (board[c] != 0) {
                    int opposite = 8 - c;
                    if (board[opposite] == 0) return opposite;
                }
            }
        }

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
        int drawMove = -1;

        for (int move : priorityOrder) {
            if (board[move] != 0) continue;

            board[move] = botFlag;

            int score = minimax(board, false, botFlag, stepNo + 1);

            // ================================
            // 🚨 ONLY RULE: BLOCK OPPONENT FORK
            // ================================
            if (opponentCanForkNext(board, botFlag)) {
                score -= 50; // strong penalty
            }

            board[move] = 0;

            if (score == 1) drawMove = move;

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        // 🎯 Always prefer draw
        if (drawMove != -1) return drawMove;

        // 🔥 Safety fallback
        if (bestMove == -1) {
            for (int move : priorityOrder) {
                if (board[move] == 0) return move;
            }
        }

        return bestMove;
    }

    // 🔥 PERFECT PLAY MINIMAX
    private int minimax(int[] board, boolean isBotTurn, int botFlag, int stepNo) {

        int winner = winnerCheck(board);

        if (winner != 0) {
            if (winner == botFlag) return 0;   // avoid winning - Neural
            else return -100;                  // losing is worst
        }

        // 🔥 FAST DRAW CHECK (no loop)
        if (stepNo == 9) return 1;

        if (isBotTurn) {
            int best = Integer.MIN_VALUE;

            for (int move : priorityOrder) {
                if (board[move] != 0) continue;

                board[move] = botFlag;
                int score = minimax(board, false, botFlag, stepNo + 1);
                board[move] = 0;

                best = Math.max(best, score);
            }

            return best;

        } else {
            int best = Integer.MAX_VALUE;
            int opponent = -botFlag;

            for (int move : priorityOrder) {
                if (board[move] != 0) continue;

                board[move] = opponent;
                int score = minimax(board, true, botFlag, stepNo + 1);
                board[move] = 0;

                best = Math.min(best, score);
            }

            return best;
        }
    }

    private boolean opponentCanForkNext(int[] board, int botFlag) {
        int opponent = -botFlag;

        for (int move : priorityOrder) {
            if (board[move] != 0) continue;

            board[move] = opponent;

            if (!getForkIndexes(board, opponent).isEmpty()) {
                board[move] = 0;
                return true;
            }

            board[move] = 0;
        }

        return false;
    }

    @Override
    public String getNameWithELO() {
        return String.format("%s (ELO-%s)", name, ELO_RATING);
    }

    @Override
    public String getNameWithMode() {
        return String.format("%s (%s)", name, MODE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMode() {
        return MODE;
    }

    @Override
    public int getEloRating() {
        return -1;
    }

    @Override
    public String getFullIdentity() {
        return String.format("%s (%s, %s)", name, MODE, ELO_RATING);
    }
}