import java.util.Scanner;

final class CommandHandler {

    private final Registry registry;
    private final AdminControl admin;
    private final EngineRenderer renderer;
    private final InputReader input;

    CommandHandler(Registry registry,
                   RankingView rankingView,
                   EngineRenderer renderer,
                   InputReader input,
                   PlayerBoardRenderer boardRenderer) {

        this.registry = registry;
        this.renderer = renderer;
        this.input = input;
        this.admin = new AdminControl(registry, rankingView, null, boardRenderer, renderer);
    }

    boolean handle(String line) {

        if (line.equalsIgnoreCase("exit")) {
            renderer.prompt("\nExiting game. Goodbye!\n");
            registry.trimToMaxPlayers();
            renderer.showExitMessage();
            System.exit(0);
        }

        if (line.equalsIgnoreCase("manage")) {
            renderer.prompt("\nProvide Password : ");
            String password = input.readLine();

            if (password.equalsIgnoreCase("123456")) {
                admin.show(new Scanner(System.in));
            } else {
                renderer.prompt("Wrong password!\n");
            }

            renderer.prompt("""
                    
                    (Continue Your Game)
                    
                    """);

            return true;
        }

        return false;
    }
}