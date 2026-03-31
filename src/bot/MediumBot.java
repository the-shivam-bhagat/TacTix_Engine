package bot;

///  Reactive (win + block) -- Win → Block → Line Extension → Random
public class MediumBot implements Bot {

    @Override
    public int chooseMove(int[] board, int playerFlag) {

        // Logic:
        // 1. If it can win → win
        // 2. Else if opponent can win → block
        // 2. Else → extend own line (row/col/diagonal scoring)
        // 3. Else → random

        return -1; // implement
    }
}