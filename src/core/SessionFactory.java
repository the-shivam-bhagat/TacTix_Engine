package core;

import auth.AuthService;
import auth.PlayerCreator;
import bot.Bot;
import exception.InvalidSessionException;
import input.Input;
import player.Player;
import player.Registry;
import renderer.view.EngineView;
import renderer.view.PlayBoardView;
import renderer.view.SessionView;
import renderer.view.SetupView;
import sessions.BotVSBotSession;
import sessions.GameSession;
import sessions.PlayerVSBotSession;
import sessions.PlayerVSPlayerSession;
import sessions.SessionContext;
import utility.Config;
import utility.Logger;

import static bot.BotFactory.createBot;

public class SessionFactory {
    private final Input input;
    private final Registry playerRegistry;
    private final SetupView renderer;
    private final SessionView sessionRenderer;
    private final PlayBoardView playBoardView;
    private final PlayerCreator playerCreator;
    private final SessionContext context;

    public SessionFactory(Input input,
                          Registry playerRegistry,
                          EngineView engineRenderer,
                          SessionView sessionRenderer,
                          PlayBoardView playBoardRenderer,
                          AuthService authService,
                          SessionContext context) {
        this.input           = input;
        this.playerRegistry  = playerRegistry;
        this.renderer        = engineRenderer;
        this.sessionRenderer = sessionRenderer;
        this.playBoardView   = playBoardRenderer;
        this.context         = context;
        this.playerCreator   = new PlayerCreator(
                input, playerRegistry, engineRenderer, authService
        );
    }

    @SuppressWarnings("UnnecessaryDefault")
    public GameSession createGameSession(SessionType type) {
        return switch (type) {
            case PLAYER_VS_PLAYER -> getPlayerVSPlayerSession();
            case PLAYER_VS_BOT   -> getPlayerVSBotSession();
            case BOT_VS_BOT      -> getBotVSBotSession();
            default -> {
                Logger.error("Invalid session type: " + type);
                throw new InvalidSessionException(type);
            }
        };
    }

    private GameSession getPlayerVSPlayerSession() {
        renderer.showSessionTypeInitialization(SessionType.PLAYER_VS_PLAYER.toString());

        Player p1 = playerCreator.createPlayer("", 1);
        Player p2 = playerCreator.createPlayer(p1.getName(), 2);

        Logger.info("Players selected: " + p1.getName() + " vs " + p2.getName());

        return new PlayerVSPlayerSession(
                p1, p2, input, playerRegistry,
                sessionRenderer, playBoardView, context
        );
    }

    private GameSession getPlayerVSBotSession() {
        renderer.showSessionTypeInitialization(SessionType.PLAYER_VS_BOT.toString());

        Player player = playerCreator.createPlayer("", 1);

        renderer.showBotsPanelViewMessage();
        renderer.showContinuePrompt();
        input.waitForEnter();

        renderer.showBotIntroduction(
                Config.BotData.TITLE,
                Config.BotData.BOT_TABLE_HEADERS,
                Config.BotData.BOT_TABLE
        );

        renderer.showBotSelectionPrompt(0);
        int level = input.readBotLevelChoice();

        Bot bot = createBot(level);
        renderer.showBotChosen(bot.getNameWithELO(), "Player 2");

        return new PlayerVSBotSession(
                player, bot, input, playerRegistry,
                sessionRenderer, playBoardView, context
        );
    }

    private GameSession getBotVSBotSession() {
        renderer.showSessionTypeInitialization(SessionType.BOT_VS_BOT.toString());
        renderer.showBotsPanelViewMessage();
        renderer.showBotIntroduction(
                Config.BotData.TITLE,
                Config.BotData.BOT_TABLE_HEADERS,
                Config.BotData.BOT_TABLE
        );

        renderer.showBotSelectionPrompt(1);
        int level1 = input.readBotLevelChoice();

        renderer.showBotSelectionPrompt(2);
        int level2 = input.readBotLevelChoice();

        Bot bot1 = level1 == level2 ? createBot(level1, true)  : createBot(level1);
        Bot bot2 = level1 == level2 ? createBot(level2, false) : createBot(level2);

        renderer.showBotChosen(bot1.getNameWithELO(), "Player 1");
        renderer.showBotChosen(bot2.getNameWithELO(), "Player 2");

        // BotVSBotSession has no human undo — no context passed
        return new BotVSBotSession(bot1, bot2, input, sessionRenderer, playBoardView);
    }
}