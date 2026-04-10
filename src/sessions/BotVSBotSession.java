package sessions;

import bot.Bot;
import core.GameBoard;
import core.GameResult;
import core.SessionType;
import input.Input;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;
import utility.Config;
import utility.Logger;

public final class BotVSBotSession implements GameSession {
    private final Bot bot1;
    private final Bot bot2;
    private final Input input;
    private final SessionView renderer;
    private final PlayBoardView playBoardView;

    private Bot first;
    private Bot second;

    private int wins1 = 0;
    private int wins2 = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";

    private static final int DOT_DELAY = Config.BotData.BOT_THINK_DOT_DELAY_MS_BVB;

    public BotVSBotSession(Bot bot1, Bot bot2, Input input, SessionView renderer, PlayBoardView playBoardView) {
        this.bot1 = bot1;
        this.bot2 = bot2;
        this.input = input;
        this.renderer = renderer;
        this.playBoardView = playBoardView;
    }

    @Override
    public void play() {
        Logger.info(String.format("New Bot v Bot session started: %s vs %s",
                bot1.getName(), bot2.getName()));

        renderer.showFirstMovePrompt(bot1.getNameWithELO(), bot2.getNameWithELO());
        boolean bot1First = input.readYesNo();

        first = bot1First ? bot1 : bot2;
        second = bot1First ? bot2 : bot1;

        int roundNumber = 0;
        boolean keepPlaying = true;

        while (keepPlaying) {
            renderer.showRoundStart(++roundNumber);
            input.waitForEnter();

            playRound(roundNumber);

            renderer.showScoreboard(
                    bot1.getName(), wins1,
                    bot2.getName(), wins2,
                    ties
            );

            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();

            // alternate first mover each round
            Bot tmp = first;
            first = second;
            second = tmp;
        }

        declareMatchResult();
        Logger.info("Match result: " + result);
    }

    @SuppressWarnings("DuplicatedCode")
    private void playRound(int roundNumber) {
        Logger.info(String.format("BvsB Round %d started: %s (X) vs %s (O)",
                roundNumber, first.getFullIdentity(), second.getFullIdentity()));

        GameBoard gameBoard = new GameBoard();

        playBoardView.showBoard(gameBoard);

        while (true) {

            int stepCount = gameBoard.getStepCount();
            Bot current = (stepCount % 2 == 0) ? first : second;
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';
            int entityFlag = (stepCount % 2 == 0) ? 1 : -1;

            int blockNo = current.chooseMove(gameBoard.getCopyOfFreq(), entityFlag, stepCount);

            renderer.showBotThinking(current.getName(), DOT_DELAY);

            gameBoard.makeMove(blockNo, entityFlag);

            playBoardView.showBoard(gameBoard);
            renderer.showBotMove(current.getName(), blockNo, mark);

            Boolean winCheck = gameBoard.checkWinner();

            if (winCheck != null) {
                Bot winner = winCheck ? first : second;
                Bot loser = winCheck ? second : first;

                recordWin(winner);
                renderer.showBotVsBotRoundWinner(
                        winner.getNameWithELO(),
                        loser.getNameWithELO()
                );

                Logger.info("Round " + roundNumber + " winner: " + winner.getFullIdentity());
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

    private void recordWin(Bot winner) {
        if (winner == bot1) wins1++;
        else wins2++;
    }

    private void declareMatchResult() {
        if (wins1 == wins2) {
            renderer.showMatchDraw();
            result = "DRAW";
        } else {
            Bot matchWinner = wins1 > wins2 ? bot1 : bot2;
            result = String.format("%s", matchWinner.getName());
            renderer.showMatchWinnerBox(result);
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                String.format("%s [%s]", bot1.getName(), bot1.getMode()),
                String.format("%s [%s]", bot2.getName(), bot2.getMode()),
                wins1,
                wins2,
                result
        );
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.BOT_VS_BOT;
    }
}