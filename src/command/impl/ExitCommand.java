package command.impl;

import command.Command;
import player.Registry;
import renderer.view.EngineView;
import utility.Logger;

public class ExitCommand implements Command {

    private final Registry registry;
    private final EngineView renderer;

    public ExitCommand(Registry registry, EngineView renderer) {
        this.registry = registry;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        renderer.showExitCommandMessage();
        registry.trimToMaxPlayers();
        renderer.showExitMessage();
        Logger.warn("System exit triggered via command");
        System.exit(0);
    }
}