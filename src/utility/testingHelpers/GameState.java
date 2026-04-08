package utility.testingHelpers;

import java.util.Arrays;

class GameState {
    int[][] states;
    int[] move;
    int playerFlag;

    GameState(int[][] game, int[] move, int playerFlag) {
        this.states = game;
        this.move = move;
        this.playerFlag = playerFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState gameState)) return false;

        return playerFlag == gameState.playerFlag &&
                Arrays.deepEquals(states, gameState.states) &&
                Arrays.equals(move, gameState.move);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(states);
        result = 31 * result + Arrays.hashCode(move);
        result = 31 * result + playerFlag;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n================ GAME STATE ================\n");

        sb.append("Final Player: ")
                .append(playerFlag == 1 ? "PROBE (X)" : "STALL (O)")
                .append("\n");

        sb.append("============================================\n");

        if (states == null) {
            sb.append("No states recorded\n");
            return sb.toString();
        }

        for (int step = 0; step < states.length; step++) {

            int[] board = states[step];
            if (board == null) break; // stop at actual game end

            int cell = (move != null && step < move.length) ? move[step] : -1;

            // Determine player at this step
            char playerChar = (step % 2 == 0) ? 'X' : 'O';
            String playerName = (step % 2 == 0) ? "PROBE" : "STALL";

            sb.append("\nStep ").append(step)
                    .append(" | ").append(playerName)
                    .append(" (").append(playerChar).append(")")
                    .append(" -> Cell ").append(cell)
                    .append("\n");

            sb.append("---------------------------------\n");

            for (int i = 0; i < 9; i++) {
                char c;
                if (board[i] == 1) c = 'X';
                else if (board[i] == -1) c = 'O';
                else c = '.';

                // Highlight move cell
                if (i == cell) {
                    sb.append("[").append(c).append("]");
                } else {
                    sb.append(" ").append(c).append(" ");
                }

                if (i % 3 != 2) sb.append("|");
                if (i % 3 == 2 && i != 8) {
                    sb.append("\n-----------\n");
                }
            }

            sb.append("\n");
        }

        sb.append("\n============================================\n");

        return sb.toString();
    }
}
