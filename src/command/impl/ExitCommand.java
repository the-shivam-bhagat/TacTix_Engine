package command.impl;

import command.Command;
import player.Registry;
import renderer.view.EngineView;

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
        System.exit(0);
    }
}