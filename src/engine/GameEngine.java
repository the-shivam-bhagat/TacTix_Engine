package engine;

import admin.AdminControl;
import command.CommandHandler;
import command.CommandProcessor;
import input.*;
import player.FilePlayerStore;
import player.Player;
import player.PlayerRegistry;

import renderer.view.EngineView;
import renderer.view.HistoryView;
import renderer.view.PlayerTableView;
import renderer.view.SessionView;

import renderer.EngineRenderer;
import renderer.HistoryRenderer;
import renderer.SessionRenderer;
import renderer.PlayerTableRenderer;

import utility.Strings;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public final class GameEngine {

    private static Input input;
    static PlayerRegistry playerRegistry;
    private static GameHistory gameHistory;

    private static EngineView engineRenderer;
    private static PlayerTableView boardRenderer;

    private static void runIntroSequence() {

        engineRenderer.showIntro();

        engineRenderer.prompt("\nDiscover the game features before we begin? (Y/N): ");
        if (input.readYesNo_Specific()) {
            engineRenderer.showFeatures();
        }

        engineRenderer.prompt("Let's see the instructions... (Press ENTER to continue)");
        input.waitForEnter();
        engineRenderer.showInstructions();

        engineRenderer.prompt("Let's take look at Current Global Leaderboard..... (Press ENTER to continue)");
        input.waitForEnter();
        displayLeaderboard();

        engineRenderer.prompt("Let's start the program..... (Press ENTER to continue) ");
        input.waitForEnter();
    }

    static void displayLeaderboard() {
        boardRenderer.showBoard(
                playerRegistry.getTopPlayers(PlayerRegistry.TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );
    }

    private static Player createPlayer(String pre, int number) {
        String name;

        while (true) {
            engineRenderer.prompt(String.format("Enter name of Player_%d : ", number));

            name = input.readLine().trim().toUpperCase();

            if (number == 2 && pre.equals(name)) {
                engineRenderer.prompt(name + " is already playing!\n");
            } else break;
        }

        return playerRegistry.getPlayer(name);
    }

    public static void main(String[] args) {

        try {
            // output blocks
            PrintStream output = new PrintStream(System.out);
            engineRenderer = new EngineRenderer(output);
            boardRenderer = new PlayerTableRenderer(output);
            SessionView sessionRenderer = new SessionRenderer(output);
            HistoryView historyRenderer = new HistoryRenderer(output);

            // players data
            playerRegistry = new PlayerRegistry(new FilePlayerStore());

            // input blocks
            Scanner sc = new Scanner(System.in);
            CommandProcessor commandHandler = new CommandHandler(
                    sc,
                    playerRegistry,
                    engineRenderer,
                    new AdminControl(
                            playerRegistry,
                            playerRegistry,
                            boardRenderer,
                            engineRenderer
                    )
            );
            input = new InputHandler(
                    sc,
                    engineRenderer,
                    commandHandler
            );

            // game block
            gameHistory = new GameHistory(historyRenderer);
            runIntroSequence();

            boolean playAnother = true;
            int gameNumber = 0;

            while (playAnother) {

                engineRenderer.showGameStart(++gameNumber);

                if (gameNumber > 1) {
                    engineRenderer.showContinuePrompt();
                    input.waitForEnter();
                } else
                    engineRenderer.printLine();

                engineRenderer.printLine();

                Player p1 = createPlayer("", 1);
                Player p2 = createPlayer(p1.getName(), 2);

                GameSession session = new GameSession(
                        p1,
                        p2,
                        input,
                        playerRegistry,
                        sessionRenderer
                );

                session.play();
                gameHistory.add(session);

                engineRenderer.showPlayAgainPrompt();
                playAnother = input.readYesNo();
                engineRenderer.printLine();
            }

            runFinalSequence();

        } catch (Exception ex) {
            engineRenderer.showError(ex.getMessage());
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement e : ex.getStackTrace())
                sb.append(e).append("\n");
            engineRenderer.showStackTrace(sb.toString());

            restart();
        }
    }

    private static void runFinalSequence() {

        engineRenderer.showHistoryPrompt();
        input.waitForEnter();

        gameHistory.showHistory();

        engineRenderer.showUpdatedLeaderboardPrompt();
        input.waitForEnterWithoutCheck();

        playerRegistry.trimToMaxPlayers();
        boardRenderer.showBoard(
                playerRegistry.getTopPlayers(PlayerRegistry.TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );

        engineRenderer.showEndingMessage();
        input.waitForEnterWithoutCheck();
    }

    public static void restart() {

        engineRenderer.showRestartPrompt();

        if (input.readYesNo()) {
            engineRenderer.showRestartingMessage();
            input.waitForEnter();

            main(new String[0]);
        } else {
            engineRenderer.showExitMessage();
            System.exit(0);
        }
    }
}