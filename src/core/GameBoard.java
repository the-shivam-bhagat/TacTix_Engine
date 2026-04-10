package core;

import static utility.Config.BoardConfig.*;

public class GameBoard {
    private static final char[][][] xoBlocks = buildBlocksXO();
    private static final int MIN_MOVES_FOR_WIN = 5;
    private static final int MAX_MOVES = 9;

    private final char[][] board;
    private final int[] freq;
    private int stepCount;

    public GameBoard() {
        this.board = getPlayBoard();
        this.freq = new int[9];
        this.stepCount = 0;
    }

    public char[][] getBoard() {
        return board.clone();
    }

    public int getStepCount() {
        return stepCount;
    }

    public boolean isCellFree(int idx) {
        return freq[idx] == 0;
    }

    public void makeMove(int blockNo, int playerFlag) {
        freq[blockNo] = playerFlag;

        placeXO(
                board,
                xoBlocks[stepCount % 2],
                CELL_START_INDEXES[blockNo]
        );

        stepCount++;
    }

    public Boolean checkWinner() {
        if (stepCount < MIN_MOVES_FOR_WIN) return null;
        return winnerCheck(freq);
    }

    public int[] getCopyOfFreq() {
        return freq.clone();
    }

    public boolean isFull() {
        return stepCount >= MAX_MOVES;
        // indexing based
    }

    //  Utility Functions

    /// winnerCheck
    private static Boolean winnerCheck(int[] freq) {
        if ((freq[0] == freq[4] && freq[0] == freq[8]) ||
                (freq[2] == freq[4] && freq[2] == freq[6])) {
            if (freq[4] == 1) return Boolean.TRUE;
            if (freq[4] == -1) return Boolean.FALSE;
        }
        for (int i = 0; i < 9; i += 3) {
            if (freq[i] == freq[i + 1] && freq[i] == freq[i + 2]) {
                if (freq[i] == 1) return Boolean.TRUE;
                if (freq[i] == -1) return Boolean.FALSE;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (freq[i] == freq[i + 3] && freq[i] == freq[i + 6]) {
                if (freq[i] == 1) return Boolean.TRUE;
                if (freq[i] == -1) return Boolean.FALSE;
            }
        }
        return null;
    }

    /// base game board
    private static char[][] getPlayBoard() {
        char[][] playBoard = new char[BOARD_HEIGHT][BOARD_LENGTH];

        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = 0; j < BOARD_LENGTH; j++)
                playBoard[i][j] = ' ';

        for (int i = 10; i < BOARD_HEIGHT; i += 11)
            for (int j = 0; j < BOARD_LENGTH; j++)
                playBoard[i][j] = '═';

        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = XO_BLOCK_LENGTH + 2; j < BOARD_LENGTH; j += XO_BLOCK_LENGTH + 3)
                playBoard[i][j] = '║';

        for (int i = 5, num = '1'; i < BOARD_HEIGHT; i += 11)
            for (int j = 13; j < BOARD_LENGTH; j += XO_BLOCK_LENGTH + 3)
                playBoard[i][j] = (char) num++;

        return playBoard;
    }

    /// placeXO
    public static void placeXO(char[][] playBoard, char[][] block, int[] indexes) {
        for (int i = 0, x = indexes[0]; i < XO_BLOCK_HEIGHT; i++, x++)
            for (int j = 0, y = indexes[1]; j < XO_BLOCK_LENGTH; j++, y++)
                playBoard[x][y] = block[i][j];
    }

    ///  X & O indexes
    private static final int[][] CELL_START_INDEXES = new int[][]{
            {1, 1}, {1, 29}, {1, 57},
            {12, 1}, {12, 29}, {12, 57},
            {23, 1}, {23, 29}, {23, 57}
    };

    /// make X and O
    private static char[][][] buildBlocksXO() {
        char[][][] blockXO = new char[2][XO_BLOCK_HEIGHT][XO_BLOCK_LENGTH];

        // X block
        for (int i = 0; i < XO_BLOCK_HEIGHT; i++)
            for (int j = 0; j < XO_BLOCK_LENGTH; j++)
                blockXO[0][i][j] = ' ';

        for (int i = 0; i < XO_BLOCK_HEIGHT; i++)
            for (int j = 0; j < XO_BLOCK_LENGTH; j += 3)
                if (i == j / 3 || i + j / 3 == 8)
                    blockXO[0][i][j] = '#';

        // O block
        for (int i = 0; i < XO_BLOCK_HEIGHT; i++)
            for (int j = 0; j < XO_BLOCK_LENGTH; j++)
                blockXO[1][i][j] = ' ';
        int[][] fill = {
                {0, 6}, {0, 9}, {0, 12}, {0, 15}, {0, 18},
                {8, 6}, {8, 9}, {8, 12}, {8, 15}, {8, 18},
                {1, 3}, {1, 21}, {2, 1}, {2, 23},
                {3, 0}, {3, 24}, {4, 0}, {4, 24}, {5, 0}, {5, 24},
                {6, 1}, {6, 23}, {7, 3}, {7, 21}
        };
        for (int[] pos : fill) blockXO[1][pos[0]][pos[1]] = '#';

        return blockXO;
    }
}