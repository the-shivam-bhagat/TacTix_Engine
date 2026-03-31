package engine;

import myUtil.Utility;

public class GameBoard {

    private final char[][] board;
    private final int[] freq;
    private int stepCount;

    public GameBoard() {
        this.board = Utility.getPlayBoard();
        this.freq = new int[9];
        this.stepCount = 0;
    }

    public char[][] getBoard() {
        return board;
    }

    public int getStepCount() {
        return stepCount;
    }

    public boolean isCellFree(int idx) {
        return freq[idx] == 0;
    }

    public void makeMove(int blockNo, char[][] xoBlock, int[] startIdx, int playerFlag) {
        freq[blockNo] = playerFlag;
        Utility.placeXO(board, xoBlock, startIdx);
        stepCount++;
    }

    public Boolean checkWinner() {
        if (stepCount < 5) return null;
        return Utility.winnerCheck(freq);
    }

    public boolean isFull() {
        return stepCount > 8;
    }
}