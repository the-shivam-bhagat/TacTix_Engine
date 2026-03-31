package admin;

import player.Registry;
import player.RankingView;
import renderer.view.EngineView;
import renderer.view.PlayerTableView;
import utility.Logger;
import utility.Strings;

import java.util.Scanner;

/// display and deletion
public final class AdminControl implements AdminService {

    private final Registry registry;
    private final RankingView rankingView;
    private final EngineView engineRenderer;
    private final PlayerTableView boardRenderer;

    public AdminControl(Registry registry,
                        RankingView rankingView,
                        PlayerTableView boardRenderer,
                        EngineView engineRenderer) {

        this.registry = registry;
        this.rankingView = rankingView;
        this.boardRenderer = boardRenderer;
        this.engineRenderer = engineRenderer;
    }

    /// Display all players and run the delete loop
    @Override
    public void show(Scanner sc) {
        Logger.info("Admin panel accessed");
        displayPlayers();
        engineRenderer.prompt("\n\n" + "-".repeat(40) + "\n");
        runDeleteLoop(sc);
        engineRenderer.prompt("\n🔒 Exiting Player Management.\n");
    }

    /// Print all registered players in ranked order
    private void displayPlayers() {
        engineRenderer.prompt(Strings.PLAYER_MANAGEMENT_BOARD);
        boardRenderer.showBoard(
                rankingView.getAllPlayers(),
                Strings.ADMIN_PLAYER_BOARD_TITLE
        );
    }

    /// Loop until the admin chooses to stop deleting
    private void runDeleteLoop(Scanner sc) {
        while (true) {
            engineRenderer.prompt("\nEnter player name to delete : ");
            String name = sc.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                engineRenderer.prompt("⚠ Player name cannot be empty.\n");
                continue;
            }

            engineRenderer.printLine();
            if (registry.deletePlayerByName(name)) {
                Logger.info("Admin init player deletion: " + name);
                engineRenderer.prompt(String.format("✅ Player '%s' deleted successfully.%n", name));
            } else {
                engineRenderer.prompt(String.format("❌ Player '%s' not found.%n", name));
            }

            engineRenderer.prompt("Delete another player? (Y/N): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty() || Character.toUpperCase(line.charAt(0)) != 'Y') break;
        }
    }
}