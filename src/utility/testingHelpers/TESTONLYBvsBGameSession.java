package utility.testingHelpers;

import bot.Bot;
import core.GameBoard;
import core.GameResult;
import sessions.Game;
import sessions.SessionType;
import input.Input;
import renderer.view.SessionView;
import utility.Config;

import java.util.*;

public final class TESTONLYBvsBGameSession implements Game {

    private final Bot bot1;
    private final Bot bot2;
    private final SessionView renderer;

    private Bot first;
    private Bot second;

    private int wins1 = 0;
    private int wins2 = 0;
    private int ties = 0;

    private static final int DOT_DELAY = Config.BotData.BOT_THINK_DOT_DELAY_MS_BVB;

    public TESTONLYBvsBGameSession(Bot bot1, Bot bot2, Input input, SessionView renderer) {
        this.bot1 = bot1;
        this.bot2 = bot2;
        this.renderer = renderer;
    }

    @Override
    public void play() {

        first = bot1;
        second = bot2;

        int roundNumber = 0;

        while (roundNumber++ < 10_000) {
            playRound();
            System.out.println(roundNumber);
            Bot tmp = first;
            first = second;
            second = tmp;
        }

        renderer.showScoreboard(
                bot1.getNameWithELO(), wins1,
                bot2.getNameWithELO(), wins2,
                ties
        );
    }

    @SuppressWarnings("DuplicatedCode")
    private void playRound() {

        GameBoard gameBoard = new GameBoard();
        while (true) {

            int stepCount = gameBoard.getStepCount();
            Bot current = (stepCount % 2 == 0) ? first : second;
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';
            int entityFlag = (stepCount % 2 == 0) ? 1 : -1;

            int blockNo = current.chooseMove(gameBoard.getCopyOfFreq(), entityFlag, stepCount);
            gameBoard.makeMove(blockNo, entityFlag);


            Boolean winCheck = gameBoard.checkWinner();

            if (winCheck != null) {
                Bot winner = winCheck ? first : second;
                recordWin(winner);
                return;
            }


            if (gameBoard.isFull()) {
                ties++;
                return;
            }
        }
    }

    private void recordWin(Bot winner) {
        if (winner == bot1) wins1++;
        else wins2++;

    }

    private void printStates(ArrayList<int[]> states) {
        int count = 0;

        for (int[] state : states) {
            if (count++ > 20) break; // limit output

            System.out.print("[");
            for (int i = 0; i < 9; i++) {
                System.out.print(state[i]);
                if (i < 8) System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                String.format("%s Bot ( %s )", bot1.getMode(), bot1.getName()),
                String.format("%s Bot ( %s )", bot2.getMode(), bot2.getName()),
                wins1,
                wins2,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public SessionType getSessionType() {
        return null;
    }
}