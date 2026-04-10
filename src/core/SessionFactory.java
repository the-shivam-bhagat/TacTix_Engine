package core;

import bot.Bot;
import exception.InvalidSessionException;
import input.Input;
import player.Player;
import player.PlayerResult;
import player.Registry;
import renderer.view.EngineView;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;
import sessions.BotVSBotSession;
import sessions.GameSession;
import sessions.PlayerVSBotSession;
import sessions.PlayerVSPlayerSession;
import utility.Config;
import utility.Logger;

import static bot.BotFactory.createBot;

public class SessionFactory {
    private final Input input;
    private final Registry playerRegistry;
    private final EngineView engineRenderer;
    private final SessionView sessionRenderer;
    private final PlayBoardView playBoardView;


    public SessionFactory(Input input, Registry playerRegistry,
                          EngineView engineRenderer,
                          SessionView sessionRenderer, PlayBoardView playBoardRenderer) {
        this.input = input;
        this.playerRegistry = playerRegistry;
        this.engineRenderer = engineRenderer;
        this.sessionRenderer = sessionRenderer;
        this.playBoardView = playBoardRenderer;
    }

    @SuppressWarnings("UnnecessaryDefault")
    public GameSession createGameSession(SessionType type) {
        return switch (type) {
            case PLAYER_VS_PLAYER -> getPlayerVSPlayerSession();
            case PLAYER_VS_BOT -> getPlayerVSBotSession();
            case BOT_VS_BOT -> getBotVSBotSession();
            default -> {
                Logger.error("Invalid session type: " + type);
                throw new InvalidSessionException(type);
            }
        };
    }

    private GameSession getPlayerVSPlayerSession() {
        engineRenderer.showSessionTypeInitialization(SessionType.PLAYER_VS_PLAYER.toString());

        Player p1 = createPlayer("", 1);
        Player p2 = createPlayer(p1.getName(), 2);

        Logger.info("Players selected: " + p1.getName() + " vs " + p2.getName());

        return new PlayerVSPlayerSession(
                p1, p2, input, playerRegistry,
                sessionRenderer, playBoardView
        );
    }

    private GameSession getPlayerVSBotSession() {
        engineRenderer.showSessionTypeInitialization(SessionType.PLAYER_VS_BOT.toString());

        Player player = createPlayer("", 1);

        engineRenderer.showBotsPanelViewMessage();
        engineRenderer.showContinuePrompt();
        input.waitForEnter();

        engineRenderer.showBotIntroduction(
                Config.BotData.TITLE,
                Config.BotData.BOT_TABLE_HEADERS,
                Config.BotData.BOT_TABLE
        );

        engineRenderer.showBotSelectionPrompt(0);
        int level = input.readBotLevelChoice();

        Bot bot = createBot(level);

        engineRenderer.showBotChosen(bot.getNameWithELO(), "Player 2");

        return new PlayerVSBotSession(
                player, bot, input, playerRegistry,
                sessionRenderer, playBoardView
        );
    }

    private GameSession getBotVSBotSession() {
        engineRenderer.showSessionTypeInitialization(SessionType.BOT_VS_BOT.toString());
        engineRenderer.showBotsPanelViewMessage();
        engineRenderer.showBotIntroduction(
                Config.BotData.TITLE,
                Config.BotData.BOT_TABLE_HEADERS,
                Config.BotData.BOT_TABLE
        );

        engineRenderer.showBotSelectionPrompt(1);
        int level1 = input.readBotLevelChoice();

        engineRenderer.showBotSelectionPrompt(2);
        int level2 = input.readBotLevelChoice();

        Bot bot1 = level1 == level2 ? createBot(level1, true) : createBot(level1);
        Bot bot2 = level1 == level2 ? createBot(level2, false) : createBot(level2);

        engineRenderer.showBotChosen(bot1.getNameWithELO(), "Player 1");
        engineRenderer.showBotChosen(bot2.getNameWithELO(), "Player 2");

        return new BotVSBotSession(
                bot1, bot2, input,
                sessionRenderer,
                playBoardView
        );
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
}
