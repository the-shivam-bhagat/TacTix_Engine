package sessions;

import bot.Bot;
import core.GameBoard;
import core.GameResult;
import core.SessionType;
import exception.GameErrorCode;
import exception.GameException;
import input.Input;
import player.Player;
import player.Registry;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;
import utility.Config;
import utility.Logger;

public final class PlayerVSBotSession implements GameSession {

    private final Player player;
    private final Bot bot;
    private final Input input;
    private final Registry registry;
    private final SessionView renderer;
    private final PlayBoardView playBoardView;

    private int playerWins = 0;
    private int botWins = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";
    private static final int DOT_DELAY = Config.BotData.BOT_THINK_DOT_DELAY_MS_PVG;

    public PlayerVSBotSession(Player player, Bot bot, Input input,
                              Registry registry, SessionView renderer, PlayBoardView playBoardView) {

        this.player = player;
        this.bot = bot;
        this.input = input;
        this.registry = registry;
        this.renderer = renderer;
        this.playBoardView = playBoardView;
    }

    @Override
    public void play() {
        Logger.info(String.format("New Player v Bot session started: %s vs %s",
                player.getName(),
                bot.getNameWithMode()
        ));

        int roundNumber = 0;
        boolean keepPlaying = true;

        while (keepPlaying) {
            renderer.showRoundStart(++roundNumber);
            input.waitForEnter();

            renderer.showFirstMovePrompt(
                    player.getName(),
                    bot.getNameWithELO()
            );

            boolean playerMove = input.readYesNo();

            playRound(roundNumber, playerMove);

            renderer.showScoreboard(
                    player.getName(),
                    playerWins,
                    bot.getNameWithELO(),
                    botWins,
                    ties
            );

            input.waitForEnter();

            renderer.showNextRoundPrompt();
            keepPlaying = input.readYesNo();
        }

        declareMatchResult();
        Logger.info("Match result: " + result);
    }

    private void playRound(int roundNumber, boolean playerMove) {
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
                blockNo = input.readCellChoice(gameBoard);

                gameBoard.makeMove(blockNo, entityFlag);

            } else {
                // Wrap bot move calls in sessions
                try {
                    blockNo = bot.chooseMove(gameBoard.getCopyOfFreq(), entityFlag, stepCount);
                    renderer.showBotThinking(bot.getName(), DOT_DELAY);

                    gameBoard.makeMove(blockNo, entityFlag);

                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    Logger.error("Bot produced invalid move: " + bot.getNameWithMode(), e);
                    throw new GameException(
                            GameErrorCode.INVALID_MOVE,
                            "Bot " + bot.getNameWithMode() + " returned an illegal move",
                            e
                    );
                }
            }

            playBoardView.showBoard(gameBoard);
            if (!playerMove) renderer.showBotMove(bot.getName(), blockNo, mark);

            Boolean winCheck = gameBoard.checkWinner();
            if (winCheck != null) {
                if (playerMove) {
                    playerWins++;
                    registry.incrementWin(player);
                    renderer.showWinner(player);
                    Logger.info("Round winner: " + player.getName());
                } else {
                    botWins++;
                    renderer.showBotWinner(bot.getNameWithELO());
                    Logger.info("Round winner: " + bot.getFullIdentity());
                }
                return;
            }

            if (gameBoard.isFull()) {
                renderer.showTie();
                ties++;
                Logger.info("Round ended in tie");
                return;
            }

            playerMove = !playerMove;
        }
    }

    private void declareMatchResult() {
        if (playerWins == botWins) {
            renderer.showMatchDraw();
            result = "DRAW";
        } else {
            result = (playerWins > botWins)
                    ? player.getName()
                    : bot.getNameWithMode();

            renderer.showMatchWinnerBox(result);
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                player.getName(),
                String.format("%s [%s]", bot.getName(), bot.getMode()),
                playerWins,
                botWins,
                result
        );
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.PLAYER_VS_BOT;
    }
}