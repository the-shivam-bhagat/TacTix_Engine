import java.util.Scanner;

/// Centralises all console I/O and handles the 'exit' command.
final class InputHandler {

    private final Scanner sc;
    private final PlayerRegistry playerRegistry;

    /// Manages the player management screen — triggered by 'manage' keyword
    private final AdminControl admin;

    InputHandler(Scanner sc, PlayerRegistry playerRegistry) {
        this.sc             = sc;
        this.playerRegistry = playerRegistry;
        this.admin = new AdminControl(playerRegistry, this);
    }

    /// Reads a line; handles 'exit' and 'manage' keywords automatically.
    String readLine() {
        String line = sc.nextLine().trim();

        // exit — persist and quit
        if (line.equalsIgnoreCase("exit")) {
            System.out.println("\nExiting game. Goodbye!");
            playerRegistry.trimToMaxPlayers();
            System.exit(0);
        }

        // manage — password-gated player management screen
        if (line.equalsIgnoreCase("manage")) {
            System.out.print("\nProvide Password : ");
            String password = sc.nextLine().trim();
            if (password.equalsIgnoreCase("123456")) {
                admin.show(sc);
            } else System.out.println("Wrong password!");
            System.out.println("""
                    
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
                System.out.print("Invalid input, please enter again : ");
            } else
                System.out.print("Invalid input, please enter again : ");
        }
    }

    void skip() {
        sc.nextLine();
    }
}