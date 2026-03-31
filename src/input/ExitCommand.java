package input;

import player.Registry;
import renderer.EngineRenderer;

public class ExitCommand implements Command {

    private final Registry registry;
    private final EngineRenderer renderer;

    public ExitCommand(Registry registry, EngineRenderer renderer) {
        this.registry = registry;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        renderer.prompt("\nExiting game. Goodbye!\n");
        registry.trimToMaxPlayers();
        renderer.showExitMessage();
        System.exit(0);
    }
}