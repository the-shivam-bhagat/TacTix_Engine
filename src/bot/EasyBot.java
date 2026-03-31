package bot;

/// Selfish strategy (no blocking) -- Win → Line Extension → Random
public class EasyBot implements Bot {

    @Override
    public int chooseMove(int[] board, int playerFlag) {

        // Logic:
        // 1. If it can win → win
        // 2. Else → extend own line (row/col/diagonal scoring)
        // 3. Else → random

        return -1; // implement
    }
}