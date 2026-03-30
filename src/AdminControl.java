import java.util.Scanner;

/// display and deletion
final class AdminControl {

    private final PlayerRegistry playerRegistry;
    private final InputHandler input;
    private final PlayerBoardRenderer boardRenderer;

    AdminControl(PlayerRegistry registry, InputHandler input, PlayerBoardRenderer boardRenderer) {
        this.playerRegistry = registry;
        this.input = input;
        this.boardRenderer = boardRenderer;
    }

    /// Display all players and run the delete loop
    void show(Scanner sc) {
        displayPlayers();
        System.out.println("\n" + "-".repeat(40));
        runDeleteLoop(sc);
        System.out.println("\n🔒 Exiting Player Management.\n");
    }

    /// Print all registered players in ranked order
    private void displayPlayers() {
        System.out.println(Strings.PLAYER_MANAGEMENT_BOARD);
        boardRenderer.showBoard(
                playerRegistry.getAllPlayers(),
                Strings.ADMIN_PLAYER_BOARD_TITLE
        );
    }

    /// Loop until the admin chooses to stop deleting
    private void runDeleteLoop(Scanner sc) {
        while (true) {
            System.out.print("\nEnter player name to delete : ");
            String name = sc.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                System.out.println("⚠ Player name cannot be empty.");
                continue;
            }

            if (playerRegistry.deletePlayerByName(name)) {
                System.out.printf("✅ Player '%s' deleted successfully.%n", name);
            } else {
                System.out.printf("❌ Player '%s' not found.%n", name);
            }

            System.out.print("Delete another player? (Y/N): ");
            if (!input.readYesNo_Specific()) break;
        }
    }
}