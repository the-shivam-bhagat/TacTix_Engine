import java.util.Iterator;
import java.util.Scanner;

/// Centralises all console I/O and handles the 'exit' command.
final class InputHandler {

    final Scanner sc;

    InputHandler(Scanner sc) {
        this.sc = sc;
    }

    /// exits if user typed "exit".
    String readLine() {
        String line = sc.nextLine().trim();
        if (line.equalsIgnoreCase("exit")) {
            System.out.println("\nExiting game. Goodbye!");
            GameEngine.playerRegistry.trimToMaxPlayers();
            System.exit(0);
        }
        if (line.equalsIgnoreCase("manage")) {
            System.out.print("\nProvide Password : ");
            String password = sc.nextLine().trim();
            if (password.equalsIgnoreCase("123456")) {
                manage();
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

    public void manage() {
        System.out.println("""
                
                ╔══════════════════════════════╗
                ║     🔧 PLAYER MANAGEMENT     ║
                ╚══════════════════════════════╝
                
                List of All the Players :-
                """);
        int rank = 0;
        for (Player player : GameEngine.playerRegistry.ranking) {
            System.out.printf("%3d) Name : %-20s , Lifetime Wins : %10d%n", ++rank, player.name, player.getLifetimeWins());
        }

        System.out.println("\n" + "-".repeat(40));

        while (true) {

            System.out.print("\nEnter player name to delete : ");
            String name = sc.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                System.out.println("⚠ Player name cannot be empty.");
                continue;
            }

            if (GameEngine.playerRegistry.deletePlayerByName(name)) {
                System.out.printf("✅ Player '%s' deleted successfully.%n", name);
            } else {
                System.out.printf("❌ Player '%s' not found.%n", name);
            }

            System.out.print("Delete another player? (Y/N): ");
            String choice = sc.nextLine().trim().toUpperCase();
            if (!choice.isEmpty() && Character.toUpperCase(choice.charAt(0)) != 'Y') break;
        }

        System.out.println("\n🔒 Exiting Player Management.\n");
    }
}