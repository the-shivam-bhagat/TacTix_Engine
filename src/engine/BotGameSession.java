package engine;

import bot.Bot;
import input.Input;
import player.Player;
import player.Registry;
import renderer.view.SessionView;
import utility.Logger;
import utility.Utility;

public final class BotGameSession implements GameSession {

    private final Player player;
    private final Bot bot;
    private final Input input;
    private final Registry registry;
    private final SessionView renderer;

    private int playerWins = 0;
    private int botWins = 0;
    private int ties = 0;

    private String result = "[Match Abandoned]";

    public BotGameSession(Player player,
                          Bot bot,
                          Input input,
                          Registry registry,
                          SessionView renderer) {

        this.player = player;
        this.bot = bot;
        this.input = input;
        this.registry = registry;
        this.renderer = renderer;
    }

    @Override
    public void play() {
        Logger.info(String.format("New Player v Bot session started: %s vs %s Bot ( %s )",
                player.getName(),
                bot.getMode(),
                bot.getName()
        ));

        int roundNumber = 0;
        boolean keepPlaying = true;

        while (keepPlaying) {
            renderer.showRoundStart(++roundNumber);
            input.waitForEnter();

            renderer.showFirstMovePrompt(player, bot);
            boolean playerMove = input.readYesNo();

            playRound(roundNumber, playerMove);

            renderer.showScoreboard(
                    player.getName(),
                    playerWins,
                    String.format("%s Bot ( %s )", bot.getMode(), bot.getName()),
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
        Logger.info("Bot Round " + roundNumber + " started");

        GameBoard gameBoard = new GameBoard();

        char[][][] xo = Utility.xo;
        int[][] idx = Utility.getStartIndexesOfEachBlock_1_to_9();

        renderer.showBoard(gameBoard.getBoard());

        while (true) {

            int stepCount = gameBoard.getStepCount();

            char mark = (stepCount % 2 == 0) ? 'X' : 'O';

            int entityFlag = (stepCount % 2 == 0) ? 1 : -1;
            int blockNo;

            if (playerMove) {
                renderer.showMovePrompt(player, mark);
                blockNo = input.readCellChoice(gameBoard);
            } else {
                blockNo = bot.chooseMove(gameBoard.getCopyOfFreq(), entityFlag, stepCount);
                renderer.showBotThinking(bot);
            }

            gameBoard.makeMove(
                    blockNo,
                    xo[stepCount % 2],
                    idx[blockNo],
                    entityFlag
            );

            Utility.displayPlayBoard(gameBoard.getBoard());

            if (!playerMove) renderer.showBotMove(bot, blockNo, mark);

            Boolean winCheck = gameBoard.checkWinner();

            if (winCheck != null) {
                if (playerMove) {
                    playerWins++;
                    registry.incrementWin(player);
                    renderer.showWinner(player);
                    Logger.info("Round winner: " + player.getName());
                } else {
                    botWins++;
                    renderer.showBotWinner(bot);
                    Logger.info(String.format("Round winner: %s Bot ( %s )",
                            bot.getMode(), bot.getName()));
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
            result = "DRAW"; // ✅ FIXED
        } else {
            result = (playerWins > botWins)
                    ? player.getName()
                    : String.format("%s Bot ( %s )", bot.getMode(), bot.getName());

            renderer.showMatchWinnerBox(result);
        }
    }

    @Override
    public GameResult toResult() {
        return new GameResult(
                player.getName(),
                String.format("%s Bot ( %s )", bot.getMode(), bot.getName()),
                playerWins,
                botWins,
                result
        );
    }
}