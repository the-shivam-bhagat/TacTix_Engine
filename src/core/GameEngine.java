package core;

import admin.AdminControl;
import command.CommandHandler;
import command.CommandProcessor;
import input.*;
import player.store.FilePlayerStore;
import player.PlayerRegistry;
import renderer.classes.*;
import renderer.view.EngineView;
import renderer.view.HistoryView;
import renderer.view.PlayerTableView;

import sessions.GameSession;
import utility.Logger;
import utility.Strings;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static player.PlayerRegistry.*;

public final class GameEngine {

    private Input input;

    private GameHistory gameHistory;

    private EngineView engineRenderer;

    private PlayerRegistry playerRegistry;

    private PlayerTableView playerTableRenderer;

    private SessionFactory sessionFactory;

    private void initialize() throws IOException {
        PrintStream output = new PrintStream(System.out);

        engineRenderer = new EngineRenderer(output);
        Logger.info("Created engine renderer");

        playerTableRenderer = new PlayerTableRenderer(output);
        Logger.info("Created board renderer");

        HistoryView historyRenderer = new HistoryRenderer(output);
        Logger.info("Created history renderer");

        playerRegistry = new PlayerRegistry(new FilePlayerStore());
        Logger.info("Created players registry");

        Scanner sc = new Scanner(System.in);

        CommandProcessor commandHandler = new CommandHandler(
                sc,
                playerRegistry,
                engineRenderer,
                new AdminControl(
                        playerRegistry,
                        playerRegistry,
                        playerTableRenderer,
                        engineRenderer
                )
        );
        Logger.info("Created command handler");

        input = new InputHandler(sc, engineRenderer, commandHandler);
        Logger.info("Created input handler");

        sessionFactory = new SessionFactory(
                input,
                playerRegistry,
                engineRenderer,
                new SessionRenderer(output),
                new PlayBoardRenderer(output)
        );
        Logger.info("Created session factory");

        gameHistory = new GameHistory(historyRenderer);
        Logger.info("Created game history");
    }

    private void runIntroSequence() {
        engineRenderer.showSystemBoot();
        input.waitForEnter();

        engineRenderer.showIntro();

        // MODULE 1
        engineRenderer.showModuleHeader(1, "Feature Overview");
        engineRenderer.showFeatureLoadPrompt();

        if (input.readYesNo_Specific()) {
            engineRenderer.showFeatureLoading();
            engineRenderer.showFeatures();
        } else {
            engineRenderer.showFeatureSkipped();
        }

        // MODULE 2
        engineRenderer.showModuleHeader(2, "AI Bot System");
        engineRenderer.showBotSystemInit();
        input.waitForEnter();
        engineRenderer.showBotsIntro();

        // MODULE 3
        engineRenderer.showModuleHeader(3, "Instruction Set");
        engineRenderer.showInstructionInit();
        input.waitForEnter();
        engineRenderer.showInstructions();

        // MODULE 4
        engineRenderer.showModuleHeader(4, "Global Leaderboard");
        engineRenderer.showLeaderboardInit();
        input.waitForEnter();
        displayLeaderboard();

        // FINAL
        engineRenderer.showSystemReady();
        input.waitForEnter();
    }

    private void displayLeaderboard() {
        playerTableRenderer.showTable(
                playerRegistry.getTopPlayers(TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );
    }

    private void startGameLoop() {
        boolean playAnother = true;
        int gameNumber = 0;

        Logger.info("Entering main game loop");

        while (playAnother) {
            engineRenderer.showSessionTypes();
            int choice = input.readSessionChoice();
            SessionType type = SessionType.fromIntValue(choice);

            GameSession session = sessionFactory.createGameSession(type);

            engineRenderer.showGameStart(++gameNumber, session.getSessionType().toString());

            Logger.info("Starting Game " + gameNumber);

            if (gameNumber > 1) {
                engineRenderer.showContinuePrompt();
                input.waitForEnter();
            } else {
                engineRenderer.printLine();
            }

            session.play();
            gameHistory.add(session);

            engineRenderer.showPlayAgainPrompt();
            playAnother = input.readYesNo();
            engineRenderer.printLine();
        }
    }

    private void shutdown() {
        runFinalSequence();
        Logger.info("Game session ended");
    }

    private void runFinalSequence() {
        engineRenderer.showHistoryPrompt();
        input.waitForEnter();

        gameHistory.showHistory();

        engineRenderer.showUpdatedLeaderboardPrompt();
        input.waitForEnterWithoutCheck();

        playerRegistry.trimToMaxPlayers();
        playerTableRenderer.showTable(
                playerRegistry.getTopPlayers(TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );

        engineRenderer.showEndingMessage();
        input.waitForEnterWithoutCheck();
    }

    private void handleFatalError(Exception ex) {
        Logger.error("Unhandled exception in GameEngine", ex);

        if (engineRenderer != null) {
            engineRenderer.showError(ex.getMessage());

            StringBuilder sb = new StringBuilder();
            for (StackTraceElement e : ex.getStackTrace())
                sb.append(e).append("\n");

            engineRenderer.showStackTrace(sb.toString());
        } else {
            System.err.println("Fatal Error: " + ex.getMessage());
        }
    }

    private boolean handleRestartDecision() {
        engineRenderer.showRestartPrompt();

        if (input.readYesNo()) {
            engineRenderer.showRestartingMessage();
            input.waitForEnter();
            return true;
        } else {
            engineRenderer.showExitMessage();
            return false;
        }
    }

    public void start() {
        boolean running = true;

        while (running) {
            try {
                Logger.info("Game Engine: System started");

                initialize();
                Logger.info("Initialization complete");

                runIntroSequence();
                startGameLoop();
                shutdown();

                running = false;

            } catch (Exception ex) {
                handleFatalError(ex);
                running = handleRestartDecision();
            }
        }
    }
}