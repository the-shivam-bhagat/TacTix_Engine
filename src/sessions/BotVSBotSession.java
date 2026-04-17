package sessions;

import bot.Bot;
import core.GameBoard;
import core.GameResult;
import core.SessionType;
import exception.GameErrorCode;
import exception.GameException;
import exception.SessionEndException;
import input.Input;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;
import utility.Config;
import utility.Logger;

import java.util.ArrayList;
import java.util.List;

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

    // Per-round parallel lists — same index = same round, null = abandoned round
    private final List<List<Integer>> allRoundMoves = new ArrayList<>();
    private final List<String> allRoundFirstPlayerStarts = new ArrayList<>(); // bot name who went first
    private final List<String> allRoundWinners = new ArrayList<>();

    private static final int DOT_DELAY = Config.BotData.BOT_THINK_DOT_DELAY_MS_BVB;

    public BotVSBotSession(Bot bot1, Bot bot2, Input input,
                           SessionView renderer, PlayBoardView playBoardView) {
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

            List<Integer> roundMoves = new ArrayList<>();

            // Record first player name before round starts
            allRoundFirstPlayerStarts.add(first.getName());

            try {
                String roundWinner = playRound(roundNumber, roundMoves); // single call
                allRoundMoves.add(new ArrayList<>(roundMoves));
                allRoundWinners.add(roundWinner);

            } catch (SessionEndException e) {
                allRoundMoves.add(null);
                allRoundWinners.add(null); // null = abandoned
                throw e;                   // propagate to GameEngine
            }

            renderer.showScoreboard(bot1.getName(), wins1, bot2.getName(), wins2, ties);
            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();

            // Alternate first mover each round
            Bot tmp = first;
            first = second;
            second = tmp;
        }

        declareMatchResult();
        Logger.info("Match result: " + result);
    }

    @SuppressWarnings("DuplicatedCode")
    private String playRound(int roundNumber, List<Integer> moves) {
        Logger.info(String.format("BvsB Round %d started: %s (X) vs %s (O)",
                roundNumber, first.getFullIdentity(), second.getFullIdentity()));

        GameBoard gameBoard = new GameBoard();
        playBoardView.showBoard(gameBoard);

        while (true) {
            int stepCount = gameBoard.getStepCount();
            Bot current = (stepCount % 2 == 0) ? first : second;
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';
            int entityFlag = (stepCount % 2 == 0) ? 1 : -1;

            int blockNo;
            // Wrap bot move calls in sessions
            try {
                blockNo = current.chooseMove(gameBoard.getCopyOfFreq(), entityFlag, stepCount);
                renderer.showBotThinking(current.getName(), DOT_DELAY);
                gameBoard.makeMove(blockNo, entityFlag);
                moves.add(blockNo);
                playBoardView.showBoard(gameBoard);
                renderer.showBotMove(current.getName(), blockNo, mark);

            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                Logger.error("Bot produced invalid move: " + current.getName(), e);
                throw new GameException(
                        GameErrorCode.INVALID_MOVE,
                        "Bot " + current.getName() + " returned an illegal move", e
                );
            }

            Boolean winCheck = gameBoard.checkWinner();
            if (winCheck != null) {
                Bot winner = winCheck ? first : second;
                Bot loser = winCheck ? second : first;
                recordWin(winner);
                renderer.showBotVsBotRoundWinner(winner.getNameWithELO(), loser.getNameWithELO());
                Logger.info("Round " + roundNumber + " winner: " + winner.getFullIdentity());
                return winner.getName();
            }

            if (gameBoard.isFull()) {
                renderer.showTie();
                ties++;
                Logger.info("Round ended in tie");
                return "TIE";
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
            result = matchWinner.getNameWithMode();
            renderer.showBotWinnerBox(result);
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                String.format("%s [%s]", bot1.getName(), bot1.getMode()),
                String.format("%s [%s]", bot2.getName(), bot2.getMode()),
                wins1, wins2, result,
                allRoundMoves, allRoundFirstPlayerStarts, allRoundWinners
        );
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.BOT_VS_BOT;
    }
}