package sessions;

import bot.Bot;
import core.GameBoard;
import core.GameResult;
import core.SessionType;
import exception.GameErrorCode;
import exception.GameException;
import exception.SessionEndException;
import exception.UndoRequestException;
import input.Input;
import player.Player;
import player.Registry;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;
import utility.Config;
import utility.Logger;

import java.util.ArrayList;
import java.util.List;

public final class PlayerVSBotSession implements GameSession {

    private final Player player;
    private final Bot bot;
    private final Input input;
    private final Registry registry;
    private final SessionView renderer;
    private final PlayBoardView playBoardView;
    private final SessionContext context;

    private int playerWins = 0;
    private int botWins = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";
    private static final int DOT_DELAY = Config.BotData.BOT_THINK_DOT_DELAY_MS_PVG;

    // Per-round parallel lists — same index = same round
    private final List<List<Integer>> allRoundMoves = new ArrayList<>();
    private final List<String> allRoundFirstPlayerStarts = new ArrayList<>();
    private final List<String> allRoundWinners = new ArrayList<>();

    public PlayerVSBotSession(Player player, Bot bot, Input input,
                              Registry registry, SessionView renderer,
                              PlayBoardView playBoardView, SessionContext context) {
        this.player = player;
        this.bot = bot;
        this.input = input;
        this.registry = registry;
        this.renderer = renderer;
        this.playBoardView = playBoardView;
        this.context = context;
    }

    @Override
    public void play() {
        Logger.info(String.format("New PvB session: %s vs %s",
                player.getName(), bot.getNameWithMode()));

        // Undo opt-in — if enabled, lifetime wins are not counted this session
        renderer.showUndoOffer();
        if (input.readYesNo_Specific()) {
            context.enableUndo();
            renderer.showUndoEnabled();
        } else {
            context.disableUndo();
            renderer.showUndoDisabled();
        }

        int roundNumber = 0;
        boolean keepPlaying = true;

        while (keepPlaying) {
            renderer.showRoundStart(++roundNumber);
            input.waitForEnter();

            renderer.showFirstMovePrompt(player.getName(), bot.getNameWithELO());
            boolean playerFirst = input.readYesNo();

            // Record who goes first
            allRoundFirstPlayerStarts.add(playerFirst ?
                    player.getName() :
                    String.format("%s [%s]", bot.getName(), bot.getMode())
            );

            List<Integer> roundMoves = new ArrayList<>();

            context.enterRound();
            try {
                String roundWinner = playRound(roundNumber, playerFirst, roundMoves);
                allRoundMoves.add(new ArrayList<>(roundMoves));
                allRoundWinners.add(roundWinner);
            } catch (SessionEndException e) {
                allRoundMoves.add(null);
                allRoundWinners.add(null); // null = abandoned
                throw e;
            } finally {
                context.exitRound();
            }

            renderer.showScoreboard(
                    player.getName(), playerWins,
                    bot.getNameWithELO(), botWins, ties);
            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();
        }

        declareMatchResult();
        Logger.info("Match result: " + result);
    }

    /// Returns winner name (player or bot), or "TIE"
    private String playRound(int roundNumber, boolean playerMove, List<Integer> moves) {
        Logger.info("PvB Round " + roundNumber + " started");

        GameBoard gameBoard = new GameBoard();
        playBoardView.showBoard(gameBoard);

        while (true) {
            int stepCount = gameBoard.getStepCount();
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';
            int entityFlag = (stepCount % 2 == 0) ? 1 : -1;
            int blockNo;

            if (playerMove) {
                renderer.showMovePrompt(player, mark);

                try {
                    blockNo = input.readCellChoice(gameBoard);
                } catch (UndoRequestException e) {
                    // PvB: playerMove is explicit — flip if odd number of moves undone
                    int undone = performUndo(gameBoard, moves);
                    if (undone % 2 == 1) //noinspection ConstantValue
                        playerMove = !playerMove;
                    playBoardView.showBoard(gameBoard);
                    continue;
                }

                gameBoard.makeMove(blockNo, entityFlag);
                moves.add(blockNo);

            } else {
                // Wrap bot move calls
                try {
                    blockNo = bot.chooseMove(gameBoard.getCopyOfFreq(), entityFlag, stepCount);
                    renderer.showBotThinking(bot.getName(), DOT_DELAY);
                    gameBoard.makeMove(blockNo, entityFlag);
                    moves.add(blockNo);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    Logger.error("Bot produced invalid move: " + bot.getNameWithMode(), e);
                    throw new GameException(GameErrorCode.INVALID_MOVE,
                            "Bot " + bot.getNameWithMode() + " returned an illegal move", e);
                }
            }

            playBoardView.showBoard(gameBoard);
            if (!playerMove) renderer.showBotMove(bot.getName(), blockNo, mark);

            Boolean winCheck = gameBoard.checkWinner();
            if (winCheck != null) {
                if (playerMove) {
                    playerWins++;
                    // Skip lifetime win increment if undo was enabled this session
                    if (!context.isUndoEnabled()) registry.incrementWin(player);
                    renderer.showWinner(player);
                    Logger.info("Round winner: " + player.getName());
                    return player.getName();
                } else {
                    botWins++;
                    renderer.showBotWinner(bot.getNameWithELO());
                    Logger.info("Round winner: " + bot.getFullIdentity());
                    return bot.getName();
                }
            }

            if (gameBoard.isFull()) {
                renderer.showTie();
                ties++;
                Logger.info("Round ended in tie");
                return "TIE";
            }

            playerMove = !playerMove;
        }
    }

    /// PvB undo: always undo up to 2 board moves (one full player turn = player + bot response).
    /// Returns the number of moves actually undone so caller can flip playerMove if odd.
    private int performUndo(GameBoard board, List<Integer> moves) {
        int movesToUndo = Math.min(2, moves.size()); // full turn = player move + bot response

        if (movesToUndo == 0) {
            renderer.showUndoNotAvailable();
            return 0;
        }

        for (int i = 0; i < movesToUndo; i++) {
            board.undo();
            //noinspection SequencedCollectionMethodCanBeUsed
            moves.remove(moves.size() - 1);
        }

        Logger.info("PvB undo: " + movesToUndo + " move(s) reversed");
        return movesToUndo;
    }

    private void declareMatchResult() {
        if (playerWins == botWins) {
            renderer.showMatchDraw();
            result = "DRAW";
        } else {
            result = (playerWins > botWins) ? player.getName() : bot.getNameWithMode();
            renderer.showMatchWinnerBox(result);
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                player.getName(),
                String.format("%s [%s]", bot.getName(), bot.getMode()),
                playerWins, botWins, result,
                allRoundMoves, allRoundFirstPlayerStarts, allRoundWinners
        );
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.PLAYER_VS_BOT;
    }
}