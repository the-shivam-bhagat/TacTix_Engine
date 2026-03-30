import java.util.Scanner;

/// Centralises all console I/O and handles the 'exit' command.
final class InputHandler {

    private final Scanner sc;
    private final Registry registry;

    /// Manages the player management screen — triggered by 'manage' keyword
    private final AdminControl admin;
    private final EngineRenderer renderer;

    InputHandler(Scanner sc,
                 Registry registry,
                 RankingView rankingView,
                 PlayerBoardRenderer boardRenderer,
                 EngineRenderer engineRenderer) {

        this.sc = sc;
        this.registry = registry;
        this.renderer = engineRenderer;
        this.admin = new AdminControl(registry, rankingView, this, boardRenderer, engineRenderer);
    }

    /// Reads a line; handles 'exit' and 'manage' keywords automatically.
    String readLine() {
        String line = sc.nextLine().trim();

        // exit — persist and quit
        if (line.equalsIgnoreCase("exit")) {
            renderer.prompt("\nExiting game. Goodbye!");
            registry.trimToMaxPlayers();
            renderer.showExitMessage();
            System.exit(0);
        }

        // manage — password-gated player management screen
        if (line.equalsIgnoreCase("manage")) {
            renderer.prompt("\nProvide Password : ");
            String password = sc.nextLine().trim();
            if (password.equalsIgnoreCase("123456")) {
                admin.show(sc);
            } else renderer.prompt("Wrong password!");
            renderer.prompt("""
                    (Your Last Input has been made an Yes or Continue)
                    
                    Continue Your Game!
                    
                    """);
            return "";
        }

        return line;
    }

    /// Waits for ENTER (any text ignored).
    void waitForEnter() {
        readLine();
    }

    /// Reads a Y/N answer. (empty == Yes.)
    boolean readYesNo() {
        String line = readLine();
        return line.isEmpty() || Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    /// Reads a Y/N answer. (empty == No.)
    boolean readYesNo_Specific() {
        String line = readLine().trim();
        return !line.isEmpty() && Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    /// Reads & validates board cell number 1–9 (non-zero means occupied)
    int readCellChoice(int[] freq) {
        while (true) {
            String input = readLine();
            if (input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '9') {
                int idx = input.charAt(0) - '1';
                if (freq[idx] == 0) return idx;
                renderer.prompt("Invalid input, please enter again : ");
            } else
                renderer.prompt("Invalid input, please enter again : ");
        }
    }

    void waitForEnterWithoutCheck() {
        sc.nextLine();
    }
}