package admin;

import auth.PasswordUtil;
import player.Player;
import player.RankingView;
import player.Registry;
import renderer.view.AdminView;
import renderer.view.PlayerTableView;
import utility.Config;
import utility.Logger;
import utility.Strings;

import java.util.List;
import java.util.Scanner;

/// Admin panel — player display and management operations
public final class AdminControl implements AdminService {

    private final Registry registry;
    private final RankingView rankingView;
    private final AdminView renderer;
    private final PlayerTableView boardRenderer;

    public AdminControl(Registry registry,
                        RankingView rankingView,
                        PlayerTableView boardRenderer,
                        AdminView engineRenderer) {
        this.registry = registry;
        this.rankingView = rankingView;
        this.boardRenderer = boardRenderer;
        this.renderer = engineRenderer;
    }

    /// Display all players then run the main admin action loop
    @Override
    public void show(Scanner sc) {
        AdminInput input = new AdminInput(sc);
        Logger.info("Admin panel accessed");

        renderer.showAdminPanelSeparator();
        renderer.prompt(Strings.ADMIN_PANEL_INTRO_BOARD);
        renderer.showAdminPanelContinuePrompt();
        sc.nextLine();

        displayPlayers(true);

        renderer.showAdminPanelSeparator();
        runAdminLoop(input);
        renderer.showAdminPanelExitMessage();
    }

    /// Print all registered players in ranked order
    private void displayPlayers(boolean printedTableFirstTime) {
        boardRenderer.showAdminTable(
                rankingView.getAllPlayers(),
                Strings.ADMIN_PLAYER_BOARD_TITLE
        );

        if (!printedTableFirstTime) renderer.showPlayerTableIsRefreshed();
    }

    // ================================================================
    // MAIN ADMIN LOOP
    // ================================================================

    private void runAdminLoop(AdminInput input) {
        while (true) {
            renderer.showAdminMenu();
            String choice = input.readChoice();

            switch (choice) {
                case "1" -> {
                    Player player = selectByName(input);
                    if (player != null) runOperationMenu(input, player);
                }
                case "2" -> {
                    Player player = selectByRank(input);
                    if (player != null) runOperationMenu(input, player);
                }
                case "3" -> displayPlayers(false); // reprint updated table
                case "0" -> { return; }
                default  -> renderer.showAdminInvalidInput();
            }

            renderer.showAdminAnotherAction();
            if (!input.readConfirm()) return;
        }
    }

    // ================================================================
    // PLAYER SELECTION
    // ================================================================

    /// Select player by entering their name
    private Player selectByName(AdminInput input) {
        renderer.showAdminSelectByNamePrompt();
        String name = input.readName();

        if (name.isEmpty()) {
            renderer.showAdminPanelEmptyNameError();
            return null;
        }

        List<Player> all = rankingView.getAllPlayers();
        for (Player p : all) {
            if (p.getName().equals(name)) return p;
        }

        renderer.showAdminPlayerNotFound(name);
        return null;
    }

    /// Select player by rank using list index — rank is 1-based, list is already sorted
    private Player selectByRank(AdminInput input) {
        renderer.showAdminSelectByRankPrompt();
        String raw = input.readNumber();

        try {
            int rank = Integer.parseInt(raw);
            List<Player> all = rankingView.getAllPlayers();

            if (rank < 1 || rank > all.size()) {
                renderer.showAdminPlayerNotFound("rank " + rank);
                return null;
            }

            return all.get(rank - 1); // rank is 1-based

        } catch (NumberFormatException e) {
            renderer.showAdminInvalidInput();
            return null;
        }
    }

    // ================================================================
    // OPERATION MENU — runs after a player is selected
    // ================================================================

    private void runOperationMenu(AdminInput input, Player player) {
        renderer.showAdminOperationMenu(player.getName());
        String choice = input.readChoice();

        switch (choice) {
            case "1" -> changeName(input, player);
            case "2" -> managePassword(input, player);
            case "3" -> setWins(input, player);
            case "4" -> deletePlayer(input, player);
            case "0" -> renderer.showAdminActionCancelled();
            default  -> renderer.showAdminInvalidInput();
        }
    }

    // ================================================================
    // OPERATION 1 — Change name
    // ================================================================

    private void changeName(AdminInput input, Player player) {
        renderer.showAdminChangeNamePrompt(player.getName());
        String newName = input.readName();

        if (newName.isEmpty()) {
            renderer.showAdminPanelEmptyNameError();
            return;
        }

        if (newName.equals(player.getName())) {
            renderer.showAdminActionCancelled();
            return;
        }

        String oldName = player.getName();
        if (!registry.renamePlayer(oldName, newName, Config.AdminConfig.ADMIN_PASSWORD)) {
            renderer.showAdminNameTaken(newName);
        } else {
            renderer.showAdminRenameSuccess(oldName, newName);
        }
    }

    // ================================================================
    // OPERATION 2 — Manage password
    // ================================================================

    private void managePassword(AdminInput input, Player player) {
        if (player.hasPassword()) {
            renderer.showAdminPasswordMenuHasPassword(player.getName());
            String choice = input.readChoice();

            switch (choice) {
                case "1" -> runPasswordSetup(input, player, true);
                case "2" -> {
                    registry.removePassword(player.getName(), Config.AdminConfig.ADMIN_PASSWORD);
                    renderer.showAdminPasswordRemoved(player.getName());
                }
                case "0" -> renderer.showAdminActionCancelled();
                default  -> renderer.showAdminInvalidInput();
            }

        } else {
            renderer.showAdminPasswordMenuNoPassword(player.getName());
            String choice = input.readChoice();

            switch (choice) {
                case "1" -> runPasswordSetup(input, player, false);
                case "0" -> renderer.showAdminActionCancelled();
                default  -> renderer.showAdminInvalidInput();
            }
        }
    }

    /// Shared password entry loop — isChange=true for change, false for add
    private void runPasswordSetup(AdminInput input, Player player, boolean isChange) {
        while (true) {
            renderer.showAdminPasswordInputPrompt(isChange);
            String newPass = input.readPassword(); // raw — preserve case

            if (newPass.isEmpty()) {
                renderer.showAdminActionCancelled();
                return;
            }

            String error = PasswordUtil.getValidationError(newPass);
            if (error != null) {
                renderer.showAdminValidationError(error);
                continue;
            }

            renderer.showAdminPasswordConfirmPrompt(isChange);
            String confirm = input.readPassword();

            if (!confirm.equals(newPass)) {
                renderer.showAdminPasswordMismatch();
                continue;
            }

            registry.setPassword(player.getName(), newPass);

            if (isChange) renderer.showAdminPasswordChanged(player.getName());
            else          renderer.showAdminPasswordAdded(player.getName());

            return;
        }
    }

    // ================================================================
    // OPERATION 3 — Set lifetime wins
    // ================================================================

    private void setWins(AdminInput input, Player player) {
        renderer.showAdminWinsCurrentAndPrompt(player.getName(), player.getLifetimeWins());
        String raw = input.readNumber();

        try {
            int wins = Integer.parseInt(raw);

            if (wins < 0) {
                renderer.showAdminWinsNegativeError();
                return;
            }

            registry.setLifetimeWins(player.getName(), wins, Config.AdminConfig.ADMIN_PASSWORD);
            renderer.showAdminWinsUpdated(player.getName(), wins);

        } catch (NumberFormatException e) {
            renderer.showAdminInvalidInput();
        }
    }

    // ================================================================
    // OPERATION 4 — Delete player
    // ================================================================

    private void deletePlayer(AdminInput input, Player player) {
        renderer.showAdminDeleteConfirmPrompt(player.getName());

        if (!input.readConfirm()) {
            renderer.showAdminActionCancelled();
            return;
        }

        if (registry.deletePlayerByName(player.getName())) {
            Logger.info("Admin deleted player: " + player.getName());
            renderer.showAdminPanelPlayerDeleted(player.getName());
        } else {
            renderer.showAdminPanelPlayerNotFound(player.getName());
        }
    }
}