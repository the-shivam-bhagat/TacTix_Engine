import java.util.Scanner;

/// display and deletion
final class AdminControl {

    private final Registry registry;
    private final InputHandler input;
    private final RankingView rankingView;
    private final EngineRenderer renderer;
    private final PlayerBoardRenderer boardRenderer;

    AdminControl(Registry registry,
                 RankingView rankingView,
                 InputHandler input,
                 PlayerBoardRenderer boardRenderer,
                 EngineRenderer renderer) {

        this.registry = registry;
        this.rankingView = rankingView;
        this.input = input;
        this.boardRenderer = boardRenderer;
        this.renderer = renderer;
    }

    /// Display all players and run the delete loop
    void show(Scanner sc) {
        displayPlayers();
        renderer.prompt("\n\n" + "-".repeat(40) + "\n");
        runDeleteLoop(sc);
        renderer.prompt("\n🔒 Exiting Player Management.\n");
    }

    /// Print all registered players in ranked order
    private void displayPlayers() {
        System.out.println(Strings.PLAYER_MANAGEMENT_BOARD);
        boardRenderer.showBoard(
                rankingView.getAllPlayers(),
                Strings.ADMIN_PLAYER_BOARD_TITLE
        );
    }

    /// Loop until the admin chooses to stop deleting
    private void runDeleteLoop(Scanner sc) {
        while (true) {
            renderer.prompt("\nEnter player name to delete : ");
            String name = sc.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                System.out.println("⚠ Player name cannot be empty.");
                continue;
            }

            if (registry.deletePlayerByName(name)) {
                renderer.prompt(String.format("✅ Player '%s' deleted successfully.%n", name));
            } else {
                renderer.prompt(String.format("❌ Player '%s' not found.%n", name));
            }

            renderer.prompt("Delete another player? (Y/N): ");
            if (!input.readYesNo_Specific()) break;
        }
    }
}