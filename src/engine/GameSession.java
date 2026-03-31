package engine;

import input.Input;
import player.Player;
import player.Registry;
import renderer.SessionRenderer;
import utility.Utility;

public final class GameSession {

    private final Player p1;
    private final Player p2;
    private final Input input;
    private final Registry registry;
    private final SessionRenderer renderer;

    private Player first;
    private Player second;

    private int wins1 = 0;
    private int wins2 = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";

    GameSession(Player p1, Player p2, Input input, Registry registry, SessionRenderer renderer) {
        this.first = this.p1 = p1;
        this.second = this.p2 = p2;
        this.input = input;
        this.registry = registry;
        this.renderer = renderer;
    }

    public void play() {
        int roundNumber = 0;
        boolean keepPlaying;

        do {
            renderer.showRoundStart(++roundNumber);
            input.waitForEnter();

            renderer.showFirstMovePrompt(first, second);

            if (!input.readYesNo()) {
                Player tmp = first;
                first = second;
                second = tmp;
            }

            playRound();

            renderer.showScoreboard(p1, wins1, p2, wins2, ties);
            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();

        } while (keepPlaying);

        declareMatchResult();
    }

    private void playRound() {

        GameBoard gameBoard = new GameBoard();

        char[][][] xo = Utility.xo;
        int[][] idx = Utility.getStartIndexesOfEachBlock_1_to_9();

        renderer.showBoard(gameBoard.getBoard());

        while (true) {

            int stepCount = gameBoard.getStepCount();

            Player current = (stepCount % 2 == 0) ? first : second;
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';

            renderer.showMovePrompt(current, mark);

            int blockNo = input.readCellChoice(gameBoard);

            int playerFlag = (stepCount % 2 == 0) ? 1 : -1;

            gameBoard.makeMove(
                    blockNo,
                    xo[stepCount % 2],
                    idx[blockNo],
                    playerFlag
            );

            Utility.displayPlayBoard(gameBoard.getBoard());

            Boolean winCheck = gameBoard.checkWinner();

            if (winCheck != null) {
                if (winCheck) {
                    renderer.showWinner(first);
                    recordWin(first);
                    registry.incrementWin(first);
                } else {
                    renderer.showWinner(second);
                    recordWin(second);
                    registry.incrementWin(second);
                }
                return;
            }

            if (gameBoard.isFull()) {
                renderer.showTie();
                ties++;
                return;
            }
        }
    }

    private void recordWin(Player player) {
        if (player == p1) wins1++;
        else wins2++;
    }

    private void declareMatchResult() {
        if (wins1 == wins2) {
            renderer.showMatchDraw();
            result = "DRAW";
        } else {
            result = wins1 > wins2 ? p1.getName() : p2.getName();
            renderer.showMatchWinnerBox(result);
        }
    }

    public GameResult toResult() {
        return new GameResult(
                p1.getName(),
                p2.getName(),
                wins1,
                wins2,
                result
        );
    }
}