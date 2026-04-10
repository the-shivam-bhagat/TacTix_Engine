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
    private final EngineView renderer;
    private final PlayerTableView boardRenderer;

    public AdminControl(Registry registry,
                        RankingView rankingView,
                        PlayerTableView boardRenderer,
                        EngineView engineRenderer) {

        this.registry = registry;
        this.rankingView = rankingView;
        this.boardRenderer = boardRenderer;
        this.renderer = engineRenderer;
    }

    /// Display all players and run the delete loop
    @Override
    public void show(Scanner sc) {
        Logger.info("Admin panel accessed");
        displayPlayers();
        renderer.showAdminPanelSeperator();
        runDeleteLoop(sc);
        renderer.showAdminPanelExitMessege();
    }

    /// Print all registered players in ranked order
    private void displayPlayers() {
        renderer.prompt(Strings.ADMIN_PANEL_INTRO_BOARD);
        boardRenderer.showTable(
                rankingView.getAllPlayers(),
                Strings.ADMIN_PLAYER_BOARD_TITLE
        );
    }

    /// Loop until the admin chooses to stop deleting
    private void runDeleteLoop(Scanner sc) {
        while (true) {
            renderer.showAdminPanelNameRequest();
            String name = sc.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                renderer.showAdminPanelEmptyNameError();
                continue;
            }

            renderer.printLine();
            if (registry.deletePlayerByName(name)) {
                Logger.info("Admin init player deletion: " + name);
                renderer.showAdminPanelPlayerDeleted(name);
            } else {
                renderer.showAdminPanelPlayerNotFound(name);
            }

            renderer.showAdminPanelDeleteAnother();
            String line = sc.nextLine().trim();
            if (line.isEmpty() || Character.toUpperCase(line.charAt(0)) != 'Y') break;
        }
    }
}