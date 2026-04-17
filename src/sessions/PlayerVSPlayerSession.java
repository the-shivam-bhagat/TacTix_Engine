package sessions;

import core.GameBoard;
import core.GameResult;
import core.SessionType;
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

public final class PlayerVSPlayerSession implements GameSession {

    private final Player p1;
    private final Player p2;
    private final Input input;
    private final Registry registry;
    private final SessionView renderer;
    private final PlayBoardView playBoardView;
    private final SessionContext context;

    private Player first;
    private Player second;

    private int wins1 = 0;
    private int wins2 = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";

    // Per-round parallel lists — all same length, same index = same round
    private final List<List<Integer>> allRoundMoves = new ArrayList<>();
    private final List<String> allRoundFirstPlayerStarts = new ArrayList<>();
    private final List<String> allRoundWinners = new ArrayList<>();

    public PlayerVSPlayerSession(Player p1, Player p2,
                                 Input input, Registry registry,
                                 SessionView renderer, PlayBoardView playBoardView,
                                 SessionContext context) {
        this.first = this.p1 = p1;
        this.second = this.p2 = p2;
        this.input = input;
        this.registry = registry;
        this.renderer = renderer;
        this.playBoardView = playBoardView;
        this.context = context;
    }

    @Override
    public void play() {
        Logger.info("New PvP session: " + p1.getName() + " vs " + p2.getName());

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
        boolean keepPlaying;

        do {
            renderer.showRoundStart(++roundNumber);
            input.waitForEnter();

            renderer.showFirstMovePrompt(first.getName(), second.getName());
            if (!input.readYesNo()) {
                Player tmp = first;
                first = second;
                second = tmp;
            }

            // Record who goes first this round before the round starts
            allRoundFirstPlayerStarts.add(first.getName());

            List<Integer> roundMoves = new ArrayList<>();

            context.enterRound();
            try {
                String roundWinner = playRound(roundNumber, roundMoves);
                allRoundMoves.add(new ArrayList<>(roundMoves));
                allRoundWinners.add(roundWinner);
            } catch (SessionEndException e) {
                allRoundMoves.add(null);
                allRoundWinners.add(null); // null = abandoned
                throw e;
            } finally {
                context.exitRound();
            }

            renderer.showScoreboard(p1.getName(), wins1, p2.getName(), wins2, ties);
            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();

        } while (keepPlaying);

        declareMatchResult();
        Logger.info("Match result: " + result);
    }

    /// Returns winner name, or "TIE"
    private String playRound(int roundNumber, List<Integer> moves) {
        Logger.info("PvP Round " + roundNumber + " started");

        GameBoard gameBoard = new GameBoard();
        playBoardView.showBoard(gameBoard);

        while (true) {
            int stepCount = gameBoard.getStepCount();
            Player current = (stepCount % 2 == 0) ? first : second;
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';
            int playerFlag = (stepCount % 2 == 0) ? 1 : -1;

            renderer.showMovePrompt(current, mark);

            int blockNo;
            try {
                blockNo = input.readCellChoice(gameBoard);
            } catch (UndoRequestException e) {
                // PvP: current player recalculated from stepCount — no playerMove to flip
                performUndo(gameBoard, moves);
                playBoardView.showBoard(gameBoard);
                continue;
            }

            gameBoard.makeMove(blockNo, playerFlag);
            moves.add(blockNo);
            playBoardView.showBoard(gameBoard);

            Boolean winCheck = gameBoard.checkWinner();
            if (winCheck != null) {
                Player winner = winCheck ? first : second;
                renderer.showWinner(winner);
                recordWin(winner);

                // Skip lifetime win increment if undo was enabled for this session
                if (!context.isUndoEnabled()) registry.incrementWin(winner);

                Logger.info("Round winner: " + winner.getName());
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

    /// PvP undo: undo MAX_UNDO_MOVES_PVP board moves.
    /// current player is re-derived from stepCount on next loop iteration — no flip needed.
    private void performUndo(GameBoard board, List<Integer> moves) {
        int movesToUndo = Math.min(Config.SessionConfig.MAX_MOVES_TO_UNDO, moves.size());

        if (movesToUndo == 0) {
            renderer.showUndoNotAvailable();
            return;
        }

        for (int i = 0; i < movesToUndo; i++) {
            board.undo();
            //noinspection SequencedCollectionMethodCanBeUsed
            moves.remove(moves.size() - 1);
        }

        Logger.info("PvP undo: " + movesToUndo + " move(s) reversed");
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
            renderer.showPlayerWinnerBox(result);
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                p1.getName(), p2.getName(), wins1, wins2, result,
                allRoundMoves, allRoundFirstPlayerStarts, allRoundWinners
        );
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.PLAYER_VS_PLAYER;
    }
}