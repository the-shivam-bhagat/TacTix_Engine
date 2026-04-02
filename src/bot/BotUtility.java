package bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public final class BotUtility {
    private BotUtility() {
    }

    static final int[][] DIRS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    static final int[] priorityOrder = {4, 0, 2, 6, 8, 1, 3, 5, 7};
    static final int[] opening = {4, 0, 2, 6, 8};
    static final int[] corners = {0, 2, 6, 8};

    public static HashSet<Integer> getWinIndexes(int[] freq, int playerFlag) {
        HashSet<Integer> set = new HashSet<>();

        // diagonals
        {
            boolean c0 = freq[0] == playerFlag;
            boolean c2 = freq[2] == playerFlag;
            boolean c4 = freq[4] == playerFlag;
            boolean c6 = freq[6] == playerFlag;
            boolean c8 = freq[8] == playerFlag;

            // left diagonal
            if (c0 && c4 && freq[8] == 0) set.add(8);
            if (c0 && c8 && freq[4] == 0) set.add(4);
            if (c4 && c8 && freq[0] == 0) set.add(0);

            // right diagonal
            if (c2 && c4 && freq[6] == 0) set.add(6);
            if (c2 && c6 && freq[4] == 0) set.add(4);
            if (c4 && c6 && freq[2] == 0) set.add(2);
        }

        // rows
        for (int i = 0; i < 9; i += 3) {
            boolean c0 = freq[i] == playerFlag;
            boolean c1 = freq[i + 1] == playerFlag;
            boolean c2 = freq[i + 2] == playerFlag;

            if (c0 && c1 && freq[i + 2] == 0) set.add(i + 2);
            if (c0 && c2 && freq[i + 1] == 0) set.add(i + 1);
            if (c1 && c2 && freq[i] == 0) set.add(i);
        }

        // columns
        for (int i = 0; i < 3; i++) {
            boolean c0 = freq[i] == playerFlag;
            boolean c3 = freq[i + 3] == playerFlag;
            boolean c6 = freq[i + 6] == playerFlag;

            if (c0 && c3 && freq[i + 6] == 0) set.add(i + 6);
            if (c0 && c6 && freq[i + 3] == 0) set.add(i + 3);
            if (c3 && c6 && freq[i] == 0) set.add(i);
        }

        return set;
    }

    public static HashSet<Integer> getExtendIndexes(int[] freq, int playerFlag) {
        HashSet<Integer> set = new HashSet<>();
        int n = 3;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int idx = i * 3 + j;
                if (freq[idx] != playerFlag) continue;

                for (int[] d : DIRS) {
                    int ni = i + d[0];
                    int nj = j + d[1];
                    if (ni < 0 || ni >= n || nj < 0 || nj >= n) continue;

                    int nextIdx = ni * 3 + nj;
                    if (freq[nextIdx] == 0) set.add(nextIdx);
                }
            }
        }

        return set;
    }

    public static HashSet<Integer> getValidIndexes(int[] freq) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < 9; i++) if (freq[i] == 0) set.add(i);
        return set;
    }

    public static int winnerCheck(int[] freq) {
        // return winner flag else 0
        if (freq[4] != 0) {
            if (freq[0] == freq[4] && freq[8] == freq[4]) return freq[4];
            if (freq[2] == freq[4] && freq[6] == freq[4]) return freq[4];
        }

        for (int i = 0; i < 9; i += 3)
            if (freq[i] != 0 && freq[i] == freq[i + 1] && freq[i] == freq[i + 2])
                return freq[i];

        for (int i = 0; i < 3; i++)
            if (freq[i] != 0 && freq[i] == freq[i + 3] && freq[i] == freq[i + 6])
                return freq[i];

        return 0;
    }

    // A fork is any empty cell where placing playerFlag creates 2+ simultaneous winning threats
    public static HashSet<Integer> getForkIndexes(int[] board, int playerFlag) {
        HashSet<Integer> forks = new HashSet<>();

        for (int move : priorityOrder) {
            if (board[move] != 0) continue;

            board[move] = playerFlag;                          // simulate move
            int threats = getWinIndexes(board, playerFlag).size(); // count threats created
            board[move] = 0;                                   // undo

            if (threats >= 2) forks.add(move);                // 2+ threats = fork
        }

        return forks;
    }

    // equimax selection — pick uniformly from a set of equally-good moves
    public static int pickRandom(HashSet<Integer> choices, Random random) {
        ArrayList<Integer> list = new ArrayList<>(choices);
        return list.get(random.nextInt(list.size()));
    }

    public static int getOpeningStrategyMove(int[] board, int step, Random random) {
        // Opening strategy
        if (step == 0) // center + corners
            return opening[random.nextInt(opening.length)];

        else if (step == 1) {
            boolean cornerTaken = board[0] != 0 || board[2] != 0 ||
                    board[6] != 0 || board[8] != 0;

            // opponent played corner → take center
            if (cornerTaken && board[4] == 0)
                return 4;

            // opponent played center → take random corner
            if (board[4] != 0)
                return corners[random.nextInt(corners.length)];

        }

        return -1;
    }
}