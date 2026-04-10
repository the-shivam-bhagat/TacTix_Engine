package renderer.classes;

import core.GameBoard;
import renderer.view.PlayBoardView;

import java.io.PrintStream;

import static utility.Config.BoardConfig.BOARD_LENGTH;
import static utility.Config.BoardConfig.BOARD_TITLE;

public class PlayBoardRenderer implements PlayBoardView {

    private final PrintStream output;

    public PlayBoardRenderer(PrintStream output) {
        this.output = output;
    }

    @Override
    public void showBoard(GameBoard board) {
        output.println("\n> [BOARD] Current game state:");
        displayPlayBoard(board.getBoard());
    }

    /// print board
    private void displayPlayBoard(char[][] playBoard) {
        System.out.println("\n╔" + "═".repeat(BOARD_LENGTH) + "╗");
        int sidePadding = (BOARD_LENGTH - BOARD_TITLE.length()) / 2;
        System.out.println("║"
                + " ".repeat(sidePadding) + BOARD_TITLE
                + " ".repeat(sidePadding + 1)
                + "║");
        System.out.println("╠" + "═".repeat(BOARD_LENGTH) + "╣");

        for (char[] row : playBoard)
            System.out.println("║" + new String(row) + "║");

        System.out.println("╚" + "═".repeat(BOARD_LENGTH) + "╝");
        System.out.println();
    }
}
