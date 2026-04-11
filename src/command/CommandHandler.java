package command;

import admin.AdminService;
import command.impl.EndCommand;
import command.impl.ExitCommand;
import command.impl.ManageCommand;
import command.impl.UndoCommand;
import player.Registry;
import renderer.view.EngineView;
import sessions.SessionContext;
import utility.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static utility.Config.CommandConfig.*;

public final class CommandHandler implements CommandProcessor {

    private final Map<String, Command> commands;

    public CommandHandler(
            Scanner sc,
            Registry registry,
            EngineView renderer,
            AdminService admin,
            SessionContext context) {

        commands = new HashMap<>();

        commands.put(EXIT,   new ExitCommand(registry, renderer));
        commands.put(MANAGE, new ManageCommand(sc, admin, renderer));
        commands.put(END,    new EndCommand(context, renderer));
        commands.put(UNDO,   new UndoCommand(context, renderer));
    }

    @Override
    public boolean handle(String line) {
        String commandKey = line.trim().toLowerCase();
        Command cmd = commands.get(commandKey);

        if (cmd == null) {
            Logger.warn("Unknown command: " + line);
            return false;
        }
        Logger.info("Command executed: " + line.toLowerCase());
        cmd.execute(); // may throw SessionEndException or UndoRequestException
        return true;
    }
}