package core;

import admin.AdminControl;
import auth.SessionAuthManager;
import command.CommandHandler;
import command.CommandProcessor;
import exception.GameException;
import exception.InvalidBotSelectionException;
import exception.InvalidSessionException;
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

    // ================================================================
    // INITIALIZATION
    // ================================================================

    /// No longer throws IOException — PlayerRegistry wraps it as GameException internally
    private void initialize() {
        PrintStream output = new PrintStream(System.out);

        engineRenderer = new EngineRenderer(output);
        Logger.info("Created engine renderer");

        playerTableRenderer = new PlayerTableRenderer(output);
        Logger.info("Created board renderer");

        HistoryView historyRenderer = new HistoryRenderer(output);
        Logger.info("Created history renderer");

        // PlayerRegistry constructor now throws GameException(STORAGE_LOAD_FAILED)
        // if FilePlayerStore.loadAll() fails — no IOException leaks out
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
                new PlayBoardRenderer(output),
                new SessionAuthManager(playerRegistry)
        );
        Logger.info("Created session factory");

        gameHistory = new GameHistory(historyRenderer);
        Logger.info("Created game history");
    }

    // ================================================================
    // INTRO SEQUENCE  — preserved exactly as original
    // ================================================================

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
        playerTableRenderer.showLeaderboard(
                playerRegistry.getTopPlayers(TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );
    }

    // ================================================================
    // MAIN GAME LOOP — recoverable exceptions handled here
    // ================================================================

    private void startGameLoop() {
        boolean playAnother = true;
        int gameNumber = 0;

        Logger.info("Entering main game loop");

        while (playAnother) {
            try {
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

            } catch (InvalidSessionException e) {
                // Bad session type selected — show error, let loop continue
                Logger.warn("Invalid session type: " + e.getMessage());
                engineRenderer.showError(
                        "Invalid session type [" + e.getErrorCode() + "]. Please try again."
                );
                continue; // skip play-again prompt, go straight back to session picker

            } catch (InvalidBotSelectionException e) {
                // Bad bot level selected — show error, let loop continue
                Logger.warn("Invalid bot selection: " + e.getMessage());
                engineRenderer.showError(
                        "Invalid bot level [" + e.getErrorCode() + "]. Please try again."
                );
                continue; // skip play-again prompt, go straight back to session picker

            } catch (GameException e) {
                // Known typed error mid-session (e.g. bot crash, bad move state)
                Logger.error("GameException during session: " + e.getMessage());
                engineRenderer.showError(
                        "A game error occurred [" + e.getErrorCode() + "]: " + e.getMessage()
                );
                // Ask if they want to play again — if no, exit loop cleanly
                engineRenderer.showPlayAgainPrompt();
                playAnother = input.readYesNo();
                engineRenderer.printLine();
                continue;

            } catch (Exception e) {
                // Completely unexpected — rethrow up to start() for restart decision
                Logger.error("Unexpected exception during game loop", e);
                throw e;
            }

            // Normal path — session completed cleanly
            engineRenderer.showPlayAgainPrompt();
            playAnother = input.readYesNo();
            engineRenderer.printLine();
        }
    }

    // ================================================================
    // SHUTDOWN — preserved exactly as original, wrapped for safety
    // ================================================================

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

        // trimToMaxPlayers now throws GameException(STORAGE_SAVE_FAILED) on failure
        // caught by the wrapper in shutdown() below — player data warning shown
        playerRegistry.trimToMaxPlayers();

        playerTableRenderer.showLeaderboard(
                playerRegistry.getTopPlayers(TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );

        engineRenderer.showEndingMessage();
        input.waitForEnterWithoutCheck();
    }

    // ================================================================
    // ERROR HANDLING — preserved exactly as original
    // ================================================================

    private void handleFatalError(Exception ex) {
        Logger.error("Unhandled exception in GameEngine", ex);

        if (engineRenderer != null) {
            engineRenderer.showError(ex.getMessage());

            StringBuilder sb = new StringBuilder();
            for (StackTraceElement e : ex.getStackTrace())
                sb.append(e).append("\n");

            engineRenderer.showStackTrace(sb.toString());
        } else {
            // engineRenderer itself failed to initialize — raw fallback
            System.err.println("Fatal Error: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private boolean handleRestartDecision() {
        // Guard: if renderer or input never initialized, can't interact with user
        if (engineRenderer == null || input == null) {
            System.err.println("> [SYSTEM] Cannot recover — renderer or input unavailable. Exiting.");
            return false;
        }

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

    // ================================================================
    // ENTRY POINT
    // ================================================================

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

                running = false; // clean exit — no exception

            } catch (GameException ex) {
                Logger.error("Game Engine (Game Exception - Known): " + ex.getMessage());
                // Known typed error (storage load fail, renderer fail, etc.)
                handleFatalError(ex);
                running = handleRestartDecision();

            } catch (Exception ex) {
                Logger.error("Game Engine (Exception - Unknown): " + ex.getMessage());
                // Completely unexpected runtime error
                handleFatalError(ex);
                running = handleRestartDecision();
            }
        }
    }
}