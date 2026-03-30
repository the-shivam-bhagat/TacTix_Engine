import java.util.Iterator;
import java.util.Scanner;

/// display and deletion
final class AdminControl {

    private final PlayerRegistry playerRegistry;
    private final InputHandler input;

    AdminControl(PlayerRegistry registry, InputHandler input) {
        this.playerRegistry = registry;
        this.input = input;
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
        Iterator<Player> players = playerRegistry.iterator();
        int rank = 0;
        while (players.hasNext()) {
            Player player = players.next();
            System.out.printf("%3d) Name : %-20s , Lifetime Wins : %10d%n",
                    ++rank, player.getName(), player.getLifetimeWins());
        }
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