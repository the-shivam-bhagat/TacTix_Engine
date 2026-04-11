package renderer.view;

public interface AdminView {
    // ── old ─────────────────────────────────────────────────────
    void showAdminPanelSeparator();

    void showAdminPanelExitMessage();

    void showAdminPanelEmptyNameError();

    void showAdminPanelPlayerNotFound(String name);

    void showAdminPanelPlayerDeleted(String name);

    void printLine();

    // ── Selection ─────────────────────────────────────────────────────
    void showAdminMenu();

    void showAdminSelectByNamePrompt();

    void showAdminSelectByRankPrompt();

    void showAdminPlayerNotFound(String identifier);

    void showAdminAnotherAction();

    // ── Operation menu ────────────────────────────────────────────────
    void showAdminOperationMenu(String playerName);

    // ── Change name ───────────────────────────────────────────────────
    void showAdminChangeNamePrompt(String currentName);

    void showAdminRenameSuccess(String oldName, String newName);

    void showAdminNameTaken(String name);

    // ── Password management ───────────────────────────────────────────
    void showAdminPasswordMenuHasPassword(String playerName);

    void showAdminPasswordMenuNoPassword(String playerName);

    void showAdminPasswordInputPrompt(boolean isChange);

    void showAdminPasswordConfirmPrompt(boolean isChange);

    void showAdminPasswordMismatch();

    void showAdminPasswordChanged(String name);

    void showAdminPasswordRemoved(String name);

    void showAdminPasswordAdded(String name);

    void showAdminValidationError(String error);

    // ── Set wins ──────────────────────────────────────────────────────
    void showAdminWinsCurrentAndPrompt(String name, int currentWins);

    void showAdminWinsUpdated(String name, int wins);

    void showAdminWinsNegativeError();

    // ── Delete ────────────────────────────────────────────────────────
    void showAdminDeleteConfirmPrompt(String playerName);

    // ── General ───────────────────────────────────────────────────────
    void showAdminInvalidInput();

    void showAdminActionCancelled();

    void showAdminPanelContinuePrompt();

    void showPlayerTableIsRefreshed();

    void prompt(String string);
}