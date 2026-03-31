package admin;

import player.Registry;
import player.RankingView;
import renderer.EngineRenderer;
import renderer.PlayerBoardRenderer;
import utility.Strings;

import java.util.Scanner;

/// display and deletion
public final class AdminControl implements AdminService {

    private final Registry registry;
    private final RankingView rankingView;
    private final EngineRenderer renderer;
    private final PlayerBoardRenderer boardRenderer;

    public AdminControl(Registry registry,
                        RankingView rankingView,
                        PlayerBoardRenderer boardRenderer,
                        EngineRenderer renderer) {

        this.registry = registry;
        this.rankingView = rankingView;
        this.boardRenderer = boardRenderer;
        this.renderer = renderer;
    }

    /// Display all players and run the delete loop
    @Override
    public void show(Scanner sc) {
        displayPlayers();
        renderer.prompt("\n\n" + "-".repeat(40) + "\n");
        runDeleteLoop(sc);
        renderer.prompt("\n🔒 Exiting Player Management.\n");
    }

    /// Print all registered players in ranked order
    private void displayPlayers() {
        renderer.prompt(Strings.PLAYER_MANAGEMENT_BOARD);
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
                renderer.prompt("⚠ Player name cannot be empty.\n");
                continue;
            }

            renderer.printLine();
            if (registry.deletePlayerByName(name)) {
                renderer.prompt(String.format("✅ Player '%s' deleted successfully.%n", name));
            } else {
                renderer.prompt(String.format("❌ Player '%s' not found.%n", name));
            }

            renderer.prompt("Delete another player? (Y/N): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty() || Character.toUpperCase(line.charAt(0)) != 'Y') break;
        }
    }
}