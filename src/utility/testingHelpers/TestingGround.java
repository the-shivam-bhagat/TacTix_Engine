package utility.testingHelpers;

import bot.*;
import command.CommandHandler;
import command.CommandProcessor;
import sessions.Game;
import input.Input;
import input.InputHandler;
import player.*;
import player.store.FilePlayerStore;
import player.store.PlayerStore;
import renderer.classes.EngineRenderer;
import renderer.classes.SessionRenderer;
import renderer.view.EngineView;
import renderer.view.SessionView;

import java.io.IOException;
import java.util.*;

public class TestingGround {

    public static void main(String[] args) throws IOException {

        // 🔹 Core dependencies
        Scanner sc = new Scanner(System.in);
        EngineView engineRenderer = new EngineRenderer(System.out);
        SessionView renderer = new SessionRenderer(System.out);
        PlayerStore store = new FilePlayerStore();
        Registry registry = new PlayerRegistry(store);
        CommandProcessor commandProcessor = new CommandHandler(sc, registry, engineRenderer, null, null);
        Input input = new InputHandler(sc, engineRenderer, commandProcessor);

        // 🔹 Create Player
        Player player = new Player("Shivam"); // or take input later

        Bot bo1 = new BeginnerBot();
        Bot bo2 = new EasyBot();
        Bot bo3 = new MediumBot();
        Bot bo4 = new HardBot();
        Bot bo5 = new UnbeatableBot();

        Bot bo7 = new StallBot();

        Bot bot1 = new BeginnerBot(true);
        Bot bot2 = new EasyBot(true);
        Bot bot3 = new MediumBot(true);
        Bot bot4 = new HardBot(true);
        Bot bot5 = new UnbeatableBot(true);
        Bot bot7 = new StallBot(true);

        Bot bot10 = new BeginnerBot(false);
        Bot bot20 = new EasyBot(false);
        Bot bot30 = new MediumBot(false);
        Bot bot40 = new HardBot(false);
        Bot bot50 = new UnbeatableBot(false);
        Bot bot70 = new StallBot(false);

        // 🔹 Create Session
        Game session = new TESTONLYBvsBGameSession(
                bo1, bo7, input, renderer
        );

//                new PlayerVSBotSession(
//                player,
//                bot,
//                input,
//                registry,
//                renderer
//        );

        // 🔹 Run Game
        session.play();

        // 🔹 (Optional) Show result object
        System.out.println("\nFinal Result:");
        System.out.println(session.toResult().toString() + "\n\n\n\n\n\n\n\n");


    }
}