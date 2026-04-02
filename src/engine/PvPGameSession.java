package engine;

import input.Input;
import player.Player;
import player.Registry;
import renderer.view.SessionView;
import utility.Logger;
import utility.Utility;

public final class PvPGameSession {

    private final Player p1;
    private final Player p2;
    private final Input input;
    private final Registry registry;
    private final SessionView renderer;

    private Player first;
    private Player second;

    private int wins1 = 0;
    private int wins2 = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";

    PvPGameSession(Player p1, Player p2, Input input, Registry registry, SessionView renderer) {
        this.first = this.p1 = p1;
        this.second = this.p2 = p2;
        this.input = input;
        this.registry = registry;
        this.renderer = renderer;
    }

    public void play() {
        Logger.info("New Player v Player session started: " + p1.getName() + " vs " + p2.getName());

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

            playRound(roundNumber);

            renderer.showScoreboard(p1.getName(), wins1, p2.getName(), wins2, ties);
            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();

        } while (keepPlaying);

        declareMatchResult();
        Logger.info("Match result: " + result);
    }

    private void playRound(int roundNumber) {
        Logger.info("PvP Round " + roundNumber + " started");

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
                Player winner = winCheck ? p1 : p2;
                renderer.showWinner(winner);
                recordWin(winner);
                registry.incrementWin(winner);
                Logger.info("Round winner: " + winner.getName());
                return;
            }

            if (gameBoard.isFull()) {
                renderer.showTie();
                ties++;
                Logger.info("Round ended in tie");
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