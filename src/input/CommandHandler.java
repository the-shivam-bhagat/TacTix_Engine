package input;

import player.Registry;
import renderer.EngineRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class CommandHandler {

    private final Map<String, Command> commands;

    public CommandHandler(
            Scanner sc,
            Registry registry,
            EngineRenderer renderer,
            AdminControl admin) {

        commands = new HashMap<>();

        // register commands
        commands.put("exit", new ExitCommand(registry, renderer));
        commands.put("manage", new ManageCommand(sc, admin, renderer));
    }

    boolean handle(String line) {
        Command cmd = commands.get(line.toLowerCase());
        if (cmd == null) return false;

        cmd.execute();
        return true;

    }
}