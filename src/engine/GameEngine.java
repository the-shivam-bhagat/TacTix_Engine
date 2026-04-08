package engine;

import admin.AdminControl;
import bot.*;
import command.CommandHandler;
import command.CommandProcessor;
import engine.sessions.BvsBGameSession;
import engine.sessions.PvsBGameSession;
import engine.sessions.PvsPGameSession;
import input.*;
import player.FilePlayerStore;
import player.Player;
import player.PlayerRegistry;

import player.PlayerResult;
import renderer.view.EngineView;
import renderer.view.HistoryView;
import renderer.view.PlayerTableView;

import renderer.EngineRenderer;
import renderer.HistoryRenderer;
import renderer.SessionRenderer;
import renderer.PlayerTableRenderer;

import utility.Config;
import utility.Logger;
import utility.Strings;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public final class GameEngine {
    private PrintStream output;

    private Input input;
    PlayerRegistry playerRegistry;
    private GameHistory gameHistory;

    private EngineView engineRenderer;
    private PlayerTableView boardRenderer;

    private void initialize() throws IOException {
        try {
            Logger.init();
        } catch (Exception e) {
            System.err.println("Logger initialization failed: " + e.getMessage());
        }

        Logger.info("System started");

        output = new PrintStream(System.out);

        engineRenderer = new EngineRenderer(output);
        boardRenderer = new PlayerTableRenderer(output);

        HistoryView historyRenderer = new HistoryRenderer(output);

        playerRegistry = new PlayerRegistry(new FilePlayerStore());

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

        input = new InputHandler(sc, engineRenderer, commandHandler);

        gameHistory = new GameHistory(historyRenderer);
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
        boardRenderer.showBoard(
                playerRegistry.getTopPlayers(PlayerRegistry.TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );
    }

    private void runGameLoop() {
        boolean playAnother = true;
        int gameNumber = 0;

        Logger.info("Entering main game loop");

        while (playAnother) {
            engineRenderer.showSessionTypes();
            int type = input.readSessionChoice();

            GameSession session = switch (type) {
                case 1 -> getPlayerVSPlayerSession();
                case 2 -> getPlayerVSBotSession();
                case 3 -> getBotVSBotSession();
                default -> {
                    Logger.error("Invalid session choice");
                    throw new IllegalStateException("Unexpected value for Game Session Type: " + type);
                }
            };

            engineRenderer.showGameStart(++gameNumber, session.getSessionType());

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

    private GameSession getPlayerVSPlayerSession() {
        engineRenderer.showSessionTypeInitialization(PvsPGameSession.sessionType);

        Player p1 = createPlayer("", 1);
        Player p2 = createPlayer(p1.getName(), 2);

        Logger.info("Players selected: " + p1.getName() + " vs " + p2.getName());

        return new PvsPGameSession(
                p1, p2, input, playerRegistry,
                new SessionRenderer(output)
        );
    }

    private GameSession getPlayerVSBotSession() {
        engineRenderer.showSessionTypeInitialization(PvsBGameSession.sessionType);

        Player player = createPlayer("", 1);

        engineRenderer.showBotsPanalViewMessage();
        engineRenderer.showContinuePrompt();
        input.waitForEnter();

        engineRenderer.showBotIntroduction(
                Config.BotData.title,
                Config.BotData.BOT_TABLE_HEADERS,
                Config.BotData.BOT_TABLE
        );

        engineRenderer.showBotSelectionPrompt(0);
        int level = input.readBotLevelChoice();

        Bot bot = getBot(level);

        engineRenderer.showBotChosen(bot.getNameWithELO(), "Player 2");

        return new PvsBGameSession(
                player, bot, input, playerRegistry,
                new SessionRenderer(output)
        );
    }

    private GameSession getBotVSBotSession() {
        engineRenderer.showSessionTypeInitialization(BvsBGameSession.sessionType);
        engineRenderer.showBotsPanalViewMessage();
        engineRenderer.showBotIntroduction(
                Config.BotData.title,
                Config.BotData.BOT_TABLE_HEADERS,
                Config.BotData.BOT_TABLE
        );

        engineRenderer.showBotSelectionPrompt(1);
        int level1 = input.readBotLevelChoice();

        engineRenderer.showBotSelectionPrompt(2);
        int level2 = input.readBotLevelChoice();

        Bot bot1 = level1 == level2 ? getBotFirstInstance(level1) : getBot(level1);
        Bot bot2 = level1 == level2 ? getBotSecondInstance(level2) : getBot(level2);

        engineRenderer.showBotChosen(bot1.getNameWithELO(), "Player 1");
        engineRenderer.showBotChosen(bot2.getNameWithELO(), "PLayer 2");

        return new BvsBGameSession(
                bot1, bot2, input,
                new SessionRenderer(output)
        );
    }

    private Bot getBot(int level) {
        return switch (level) {
            case 1 -> new BeginnerBot();
            case 2 -> new EasyBot();
            case 3 -> new MediumBot();
            case 4 -> new HardBot();
            case 5 -> new UnbeatableBot();
            case 0 -> new StallBot();
            default -> {
                Logger.error("Invalid session choice");
                throw new IllegalStateException("Unexpected value for Bot Type: " + level);
            }
        };
    }

    private Bot getBotFirstInstance(int level) {
        return switch (level) {
            case 1 -> new BeginnerBot(true);
            case 2 -> new EasyBot(true);
            case 3 -> new MediumBot(true);
            case 4 -> new HardBot(true);
            case 5 -> new UnbeatableBot(true);
            case 0 -> new StallBot(true);
            default -> {
                Logger.error("Invalid Bot Selection (First Instance)");
                throw new IllegalStateException("Unexpected value for Bot (First Instance) Type: " + level);
            }
        };
    }

    private Bot getBotSecondInstance(int level) {
        return switch (level) {
            case 1 -> new BeginnerBot(false);
            case 2 -> new EasyBot(false);
            case 3 -> new MediumBot(false);
            case 4 -> new HardBot(false);
            case 5 -> new UnbeatableBot(false);
            case 0 -> new StallBot(false);
            default -> {
                Logger.error("Invalid Bot Selection (Second Instance)");
                throw new IllegalStateException("Unexpected value for Bot (Second Instance) Type: " + level);
            }
        };
    }


    private Player createPlayer(String pre, int number) {
        String name;

        while (true) {
            engineRenderer.requestPlayerName(number);
            String input = this.input.readLine();
            if (input == null) continue;
            name = input.trim().toUpperCase();

            if (number == 2 && pre.equals(name)) {
                engineRenderer.showPlayerAlreadyInGame(name);
            } else break;
        }

        PlayerResult result = playerRegistry.getOrCreatePlayer(name);
        Player player = result.getPlayer();

        if (result.isNew()) {
            engineRenderer.showNewPlayerWelcome(player);
        } else {
            engineRenderer.showReturningPlayerWelcome(player);
        }

        return player;
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
        boardRenderer.showBoard(
                playerRegistry.getTopPlayers(PlayerRegistry.TOP_PLAYERS),
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
            for (StackTraceElement e : ex.getStackTrace()) {
                sb.append(e).append("\n");
            }

            engineRenderer.showStackTrace(sb.toString());
        } else {
            System.err.println("Fatal Error: " + ex.getMessage());
        }
    }

    private void restart() {
        engineRenderer.showRestartPrompt();

        if (input.readYesNo()) {
            engineRenderer.showRestartingMessage();
            input.waitForEnter();
            start();
        } else {
            engineRenderer.showExitMessage();
            System.exit(0);
        }
    }

    public void start() {
        try {
            initialize();
            runIntroSequence();
            runGameLoop();
            shutdown();
        } catch (Exception ex) {
            handleFatalError(ex);
            restart();
        }
    }
}