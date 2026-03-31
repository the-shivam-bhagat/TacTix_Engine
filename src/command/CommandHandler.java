package command;

import admin.AdminService;
import command.impl.ExitCommand;
import command.impl.ManageCommand;
import player.Registry;
import renderer.view.EngineView;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class CommandHandler implements CommandProcessor {

    private final Map<String, Command> commands;

    public CommandHandler(
            Scanner sc,
            Registry registry,
            EngineView renderer,
            AdminService admin) {

        commands = new HashMap<>();

        // register commands
        commands.put("exit", new ExitCommand(registry, renderer));
        commands.put("manage", new ManageCommand(sc, admin, renderer));
    }

    @Override
    public boolean handle(String line) {
        Command cmd = commands.get(line.toLowerCase());
        if (cmd == null) return false;

        cmd.execute();
        return true;

    }
}